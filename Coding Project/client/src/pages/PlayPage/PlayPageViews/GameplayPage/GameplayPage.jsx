import './GameplayPage.css';
import PlayerTag from "../../../../components/PlayerTagComponent/PlayerTag.jsx";
import BattleShipBoard from "../../../../components/BoardComponent/BattleShipBoard.jsx";
import ShopComponent from "../../../../components/ShopComponent/ShopComponent.tsx";
import {getCookie} from "../../../../../utils/cookies.js";
import {useCallback, useEffect, useState} from "react";
import PropTypes from "prop-types";
import {useWebSocket} from "../../../../components/WebsocketContextProvider.jsx";
import GameHint from "./GameHint.jsx";
import { useNavigate } from 'react-router-dom';

const GameplayPage = ({playerData, sessionId, pageView}) => {
    const navigate = useNavigate();

    const lobbyCode = getCookie('lobbyCode');

    let pb = Array(10).fill(null).map(() => Array(10).fill("empty"));
    const [playerBoard, setPlayerBoard] = useState(pb);

    // Handles who's board is being displayed for serverBoard
    const initialServerPlayerIndex = (playerData.findIndex(player => player.id === sessionId) + 1) % playerData.length;
    const [serverPlayerIndex, setServerPlayerIndex] = useState(initialServerPlayerIndex);
    const [serverPlayer, setServerPlayer] = useState(playerData[serverPlayerIndex]);
    const [serverPlayerName, setServerPlayerName] = useState(playerData[serverPlayerIndex]?.displayName);
    const [gameStart, setGameStart] = useState(false);
    const [openShop, setOpenShop] = useState(false);
    const [playerPoints, setPlayerPoints] = useState(0);
    const [turn, setTurn] = useState(playerData[0].id);
    const [serverBoard, setServerBoard] = useState(pb);
    const {sockets} = useWebSocket()
    const [selectedPowerUp, setSelectedPowerUp] = useState("Default");

    const [gameHintVisible, setGameHintVisible] = useState(false);
    const [gameHintMessage, setGameHintMessage] = useState('');

    const socket = sockets[lobbyCode];


    const winnerSequence = useCallback(() => {
        setTimeout(() => {
            navigate('/entry');
        }, 5000);
    }, []);

    useEffect(() => {
        if (playerData[serverPlayerIndex]) {
            setServerPlayerName(playerData[serverPlayerIndex].displayName || 'Unknown Player');
        }
    }, [playerData, serverPlayerIndex]);

    useEffect(() => {
        playerData.forEach(player => {
            if (player.id === sessionId) {
                setPlayerBoard(player.gameBoard.playerBoard);
                setPlayerPoints(player.gameBoard.points);
            }
        });
    }, [playerData, sessionId]);

    useEffect(() => {
        setServerBoard(playerData[serverPlayerIndex]?.gameBoard.serverBoard);
        setServerPlayer(playerData[serverPlayerIndex]);
        setServerPlayerName(playerData[serverPlayerIndex].displayName);
    }, [playerBoard, playerData, serverPlayerIndex]);

    useEffect(() => {
        const alivePlayers = playerData.filter(player => player.gameBoard.playerLife);
        if (alivePlayers.length <= 1) {
            winnerSequence();
        }
        setSelectedPowerUp(playerData.find(player => player.id === sessionId).gameBoard.selectedPowerUp);
    }, [playerData, winnerSequence]);

    useEffect(() => {
        if (pageView === 'gameplay') {
            setGameStart(true);
            startGame(lobbyCode).then(() => {
                console.log('Game started');
            });
        }
    }, [pageView]);

    const startGame = async (lobbyCode) => {
        const url = `${import.meta.env.VITE_WEBSOCKET_URL}/start/${lobbyCode}`;
        try {
            const response = await fetch(url, {
                method: 'POST',
            });
            if (!response.ok) {
                console.log("ERROR: Game start failed ion GameplayPage.jsx")
            }
        } catch (error) {
            console.error('Error starting the game:', error);
        }
    };

    useEffect(() => {
        const handleTurnUpdate = (message) => {
            const data = JSON.parse(message.data);
            if (data.type === 'TURN_UPDATE') {
                setTurn(data.playerId);
            }
        };

        if (socket) {
            socket.addEventListener('message', handleTurnUpdate);
        }

        return () => {
            if (socket) {
                socket.removeEventListener('message', handleTurnUpdate);
            }
        };
    }, [socket]);

    const hitCell = (row, col) => {
        if (socket) {
            const message = JSON.stringify({
                type: 'HIT_CELL',
                row,
                col,
                serverPlayer: serverPlayer.id,
                color: playerData.find(player => player.id === sessionId).color
            });
            socket.send(message);
        }
    }

    const sendNewSelectedPowerUp = (powerUp) => {
        if (selectedPowerUp !== powerUp) {
            if (socket) {
                const message = JSON.stringify({
                    type: 'SELECT_POWER_UP',
                    powerUp,
                });
                socket.send(message);
            }
            setSelectedPowerUp(powerUp);
        }
    };

    const handleServerCellClick = (row, col) => {
        console.log(`Cell clicked: Row ${row}, Col ${col}`);
        hitCell(row, col);
    };

    const handleCellClick = (row, col) => {
        console.log(`Cell clicked: Row ${row}, Col ${col}`);
    };

    const handleLeftArrowClick = () => {
        let newIndex = (serverPlayerIndex - 1 + playerData.length) % playerData.length;
        while (playerData[newIndex].id === sessionId || !playerData[newIndex].gameBoard.playerLife) {
            newIndex = (newIndex - 1 + playerData.length) % playerData.length;
        }
        setServerPlayerIndex(newIndex);
        setServerBoard(playerData[newIndex].gameBoard.serverBoard);
        setServerPlayer(playerData[newIndex]);
        setServerPlayerName(playerData[newIndex]?.displayName);
    };

    const handleRightArrowClick = () => {
        const alivePlayers = playerData.filter(player => player.gameBoard.playerLife);
        if (alivePlayers.length <= 1) {
            if (alivePlayers[0]?.id === sessionId) {
                // Show the client's own board
                setServerPlayerIndex(playerData.findIndex(player => player.id === sessionId));
                setServerBoard(clientPlayer.gameBoard.serverBoard);
                setServerPlayer(clientPlayer);
                setServerPlayerName(clientPlayer.displayName);
            } else {
                console.log("Only one player alive. Cannot switch to another player.");
            }
            return;
        }

        let newIndex = (serverPlayerIndex + 1) % playerData.length;
        while (playerData[newIndex].id === sessionId || !playerData[newIndex].gameBoard.playerLife) {
            newIndex = (newIndex + 1) % playerData.length;
        }
        setServerPlayerIndex(newIndex);
        setServerBoard(playerData[newIndex].gameBoard.serverBoard);
        setServerPlayer(playerData[newIndex]);
        setServerPlayerName(playerData[newIndex]?.displayName);
    };

    useEffect(() => {
        if (!serverPlayer.gameBoard.playerLife) {
            handleRightArrowClick();
        }
    }, [serverPlayer.gameBoard.playerLife]);

    const clientPlayer = playerData.find(player => player.id === sessionId);

    useEffect(() => {
        if (gameStart) {
            let timer;
            // CHANGE THIS WHENEVER WE CHANGE TURN TIME
            setCountdown(20);
            timer = setInterval(() => {
                setCountdown(prevCountdown => {
                    if (prevCountdown <= 1) {
                        clearInterval(timer);
                        return 0;
                    }
                    return prevCountdown - 1;
                });
            }, 1000);

            return () => {
                clearInterval(timer);
            };
        }
    }, [turn, gameStart]);

    const [countdown, setCountdown] = useState(10);

    if (!clientPlayer) {
        return <div>Loading...</div>;
    }

    function handleClose(){
        if (openShop) {
            setOpenShop(false);
        }
    }


    const handlePlayerTagClick = (playerId) => {
        const newIndex = playerData.findIndex(player => player.id === playerId);
        if (newIndex !== -1 && playerId !== sessionId && playerData[newIndex].gameBoard.playerLife) {
            setServerPlayerIndex(newIndex);
            setServerBoard(playerData[newIndex].gameBoard.serverBoard);
            setServerPlayer(playerData[newIndex]);
            setServerPlayerName(playerData[newIndex]?.displayName);
        }
    };

    const handlePowerUpClick = (powerUp) => {
        console.log(`Power-up clicked: ${Object.keys(powerUp)[0]}`);
        setSelectedPowerUp(Object.keys(powerUp)[0]);
        sendNewSelectedPowerUp(Object.keys(powerUp)[0]);
    };

    const getPowerUpClassName = (powerUpName) => {
        return `power-up-${powerUpName.toLowerCase()}`;
    };

    return (
        <>
            <div className={`container ${!gameStart ? 'disabled' : ''}`}>
                <section className={'section'}>
                    <div className={'turn-timer'}>
                        {countdown}
                    </div>
                    <div className={'players-container'}>
                        {playerData.map((player) => (
                            <div className={`${turn === player.id ? 'player-tag-container' : ''}`} key={player.id} onClick={() => handlePlayerTagClick(player.id)}>
                                <PlayerTag
                                    displayName={`${player.displayName}`}
                                    key={player.id}
                                    color={player.gameBoard.playerLife ? player.color : "#ff0000"}
                                    isClient={player.id === sessionId}
                                />
                            </div>
                        ))}
                    </div>
                    <div className="shop-button-container">
                        <button
                            className="shop-button"
                            onClick={() => setOpenShop(true)}
                        >
                            <img id="shop-icon" src="/shopping-cart.svg" alt="Shop"/>
                            Shop
                        </button>
                        <div className="player-points">
                            {playerPoints} Points
                        </div>
                    </div>
                    <div className={`boards-container ${!gameStart ? 'gap-20' : 'gap-10'}`}>
                        <div className={`boards-container ${!gameStart ? 'gap-20' : 'gap-10'}`}>
                            <div className={'client-board'}>
                                <h3 style={{color: clientPlayer?.gameBoard?.playerLife ? clientPlayer.color : 'red'}}>Your
                                    Board</h3>
                                <div className={`${turn !== sessionId ? 'cant-click' : ''}`}>
                                    <BattleShipBoard board={playerBoard} onClick={handleCellClick}/>
                                </div>
                            </div>
                            <div className={'power-up-container'}>
                                <p> Selected: <strong>{selectedPowerUp}</strong></p>
                                {clientPlayer?.gameBoard?.powerUpInventory?.map((powerUp, index) => (
                                    <div key={index} className={getPowerUpClassName(Object.keys(powerUp)[0])}
                                         onClick={() => handlePowerUpClick(powerUp)}>
                                        {Object.keys(powerUp)[0]}: {Object.values(powerUp)[0]}
                                    </div>
                                ))}
                                <div className={'reset-power-ups-button'} onClick={() => {
                                    setSelectedPowerUp("Default");
                                    sendNewSelectedPowerUp("Default");

                                }}>
                                    Default
                                </div>
                            </div>
                            <div className={'opponent-board'}>
                                <div className={'toggle-player-boards'}>
                                    <span className="arrow left-arrow" onClick={handleLeftArrowClick}>⬅</span>
                                    <h3 className="player-name"
                                        style={{color: serverPlayer.gameBoard.playerLife ? serverPlayer.color : "#ff0000"}}>{serverPlayerName}&apos;s
                                        Board</h3>
                                    <span className="arrow right-arrow" onClick={handleRightArrowClick}>➡</span>
                                </div>
                                <div className={`${turn !== sessionId ? 'cant-click' : ''}`}>
                                    <BattleShipBoard board={serverBoard} onClick={handleServerCellClick}/>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
            <GameHint message={gameHintMessage} visible={gameHintVisible}/>
            {openShop &&
                <div className="shopComponent-container"><ShopComponent points={playerPoints} handleClose={handleClose}
                                                                        /></div>}
        </>
    );
};


GameplayPage.propTypes = {
    playerData: PropTypes.array.isRequired,
    sessionId: PropTypes.string.isRequired,
    pageView: PropTypes.string.isRequired
};

export default GameplayPage;