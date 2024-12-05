import {useEffect, useRef, useState} from "react";
import { useWebSocket } from '../../components/WebsocketContextProvider';
import { getCookie } from "../../../utils/cookies.js";
import './PlayPage.css';
import {LobbyView} from "./PlayPageViews/LobbyView/LobbyView.jsx";
import ShipPlacementPanel from "./PlayPageViews/ShipPlacementPanel/ShipPlacement.jsx";
import PropTypes from "prop-types";
import GameplayPage from "./PlayPageViews/GameplayPage/GameplayPage.jsx";
import Toast from "../../components/Toast/Toast.jsx"
import ReactConfetti from 'react-confetti';





const PlayPage = ({sessionId}) => {
    const [players, setPlayers] = useState([]);
    // const [sessionId, setSessionId] = useState(null);
    const { connectToLobby, disconnectFromLobby, sockets } = useWebSocket();
    const lobbyCode = getCookie('lobbyCode');
    const [ready , setReady] = useState(false);
    const [timer, setTimer] = useState(0);
    const [allReady, setAllReady] = useState(false);
    const [minPlayersReached, setMinPlayersReached] = useState(false);
    const [allShipsPlaced, setAllShipsPlaced] = useState(false);
    const [toastType, setToastType] = useState('');
    const [toastMessage, setToastMessage] = useState('');
    const [toastVisible, setToastVisible] = useState(true);
    const yourPlayer = players.find(player => player.id === sessionId) || { displayName: 'Unknown Player', color: 'gray' };
    const [pageView, setPageView] = useState('lobby');
    const [fadeOut, setFadeOut] = useState(false);
    const [playerData, setPlayerData] = useState([]);
    const socket = sockets[lobbyCode];
    const colorMapRef = useRef({});
    const [showConfetti, setShowConfetti] = useState(false);


    const playerColors = ['#9120fa', '#FFD700', '#1baeed', '#07ff00'];

    const addPowerUps = () => {
        const socket = sockets[lobbyCode]
        if (socket) {

            const message = JSON.stringify({
                type: 'PLACE_POWERUPS',
            });
            socket.send(message);
        }
    };

    useEffect(() => {
        connectToLobby(lobbyCode);
        const socket = sockets[lobbyCode];

        if (socket) {
            const handleSocketMessage = (event) => {
                try {
                    const data = JSON.parse(event.data);

                    if (data.sessionId) {
                        // setSessionId(data.sessionId);
                    } else if (Array.isArray(data)) {
                        console.log(data);

                        // Assign colors consistently based on player ID
                        data.forEach((player) => {
                            if (!colorMapRef.current[player.id]) {
                                colorMapRef.current[player.id] = playerColors[Object.keys(colorMapRef.current).length % playerColors.length];
                            }
                            player.color = colorMapRef.current[player.id];
                        });



                        // Ensure 'ready' is defined, and add placeholders if players are less than 4
                        const updatedPlayers = data.map(player => ({
                            ...player,
                            ready: player.ready !== undefined ? player.ready : false,
                        }));
                        const fullPlayerList = updatedPlayers.concat(
                            Array(4 - updatedPlayers.length).fill({ displayName: 'Waiting for player...', ready: false })
                        );

                        setPlayerData(data);
                        setPlayers(fullPlayerList);

                        // Update minPlayersReached and game readiness state
                        setMinPlayersReached(updatedPlayers.length >= 2);
                        setAllReady(updatedPlayers.every(player => player.ready));
                        setAllShipsPlaced(updatedPlayers.every(player => player.areShipsPlaced));
                    }
                } catch (error) {
                    console.error("Error processing WebSocket data:", error.message);
                    setPlayers(Array(4).fill({ displayName: 'Waiting for player...', ready: false }));
                }
            };

            socket.addEventListener('message', handleSocketMessage);

            return () => {
                socket.removeEventListener('message', handleSocketMessage);
                disconnectFromLobby(lobbyCode);
            };
        }
    }, [lobbyCode, connectToLobby, disconnectFromLobby, sockets]);

    const showToast = (type) => {
        setToastType(type);
        setToastVisible(true);
        setTimeout(() => {
            setToastVisible(false);
        }, 2000);
    };

    useEffect(() => {
        let timerId;
        if (allReady && minPlayersReached) {
            console.log('all players are ready')
            setTimer(5);
            timerId = setInterval(() => {
                setTimer(prevTimer => {
                    if (prevTimer <= 1) {
                        clearInterval(timerId);
                        setPageView('place-ships');
                        console.log("All players are ready. Timer ended.");
                        return 0;
                    }
                    return prevTimer - 1;
                });
            }, 1000);
        } else {
            setTimer(0);
        }
        return () => clearInterval(timerId);
    }, [allReady]);

    useEffect(() => {
        if (socket) {
            const handleMessage = (event) => {
                try {
                    const data = JSON.parse(event.data);
                    if (data.type === 'PLAYER_DEATH') {
                        console.log("Player " + data.playerName + "\n" + "PlayerId " + data.playerId + "\n" + "has fallen!");
                        playerData.forEach((player) => {
                            if (player.id === data.playerId) {
                                player.isAlive = false;
                            }
                        });
                    } else if (data.type === 'RESULT') {
                        setToastMessage(data.message);
                        showToast('success');
                        if (data.message === 'You won!') {
                            setShowConfetti(true);
                        }
                    }
                } catch (e) {
                    console.error('Error parsing incoming message: ', e);
                }
            };

            socket.addEventListener('message', handleMessage);

            return () => {
                socket.removeEventListener('message', handleMessage);
            };
        }
    }, [socket]);

    useEffect(() => {
        if (allShipsPlaced) {
            addPowerUps()
            setToastMessage('Initializing Game.');
            showToast('success');
            setTimeout(() => {
                setFadeOut(true);

                setTimeout(() => {
                    setPageView('gameplay');
                }, 1000);
            }, 2000);
        }
    },[allShipsPlaced]);

    function onReadyUp() {
        const socket = sockets[lobbyCode];
        let type;
        if (!ready){
            type = 'success';
            setToastMessage('You are ready!');
        }else{
            type = 'error'
            setToastMessage('You are no longer ready!');
        }

        showToast(type);
        if (socket) {
            const message = JSON.stringify({ type: 'READY_UP' });
            socket.send(message);
        }
    }

    return (
        <div>
            {showConfetti && <ReactConfetti />}
            <div className={'container'}>
                {/*TODO: Make sure each player is allowed to be in the lobby/server by
                checking sessionId/playerId exists in lobby,
                if not then redirect them to enterLobbyCode screen*/}

                <Toast message={toastMessage} type={toastType} visible={toastVisible} />
                {pageView === 'lobby' &&
                    <LobbyView onReadyUp={onReadyUp}
                               minPlayersReached={minPlayersReached}
                               allReady={allReady}
                               players={players}
                               setReady={setReady}
                               timer={timer}
                               ready={ready}
                               sessionId={sessionId}/>
                }
                {pageView === 'place-ships' &&
                    <div className={fadeOut ? 'fade-out' : ''}>
                        <ShipPlacementPanel sessionId={sessionId} displayName={yourPlayer.displayName} lobbyCode={lobbyCode} color={yourPlayer.color}/>
                    </div>
                }

                {pageView === 'gameplay' &&
                        <GameplayPage pageView={pageView} playerData={playerData} sessionId={sessionId}/>
                }
            </div>
        </div>
    );
}

export default PlayPage;

PlayPage.propTypes = {
    sessionId: PropTypes.string.isRequired
}