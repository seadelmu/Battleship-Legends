import { useState, useEffect } from 'react';
import './ShipPlacement.css';
import PlayerTag from "../../../../components/PlayerTagComponent/PlayerTag.jsx";
import BattleShipBoard from "../../../../components/BoardComponent/BattleShipBoard.jsx";
import PropTypes from "prop-types";
import {getCookie} from "../../../../../utils/cookies.js";
import {useWebSocket} from "../../../../components/WebsocketContextProvider.jsx";

const ShipPlacementPanel = ({sessionId, displayName, lobbyCode, color}) => {
    const ships = [
        // real ships for demo
        { id: '1', size: 2, orientation: 'horizontal' },
        { id: '2', size: 3, orientation: 'horizontal' },
        { id: '3', size: 3, orientation: 'horizontal' },
        { id: '4', size: 4, orientaggittion: 'horizontal' },
        { id: '5', size: 5, orientation: 'horizontal' }

        // test to make it quicker
        // { id: '1', size: 3, orientation: 'horizontal' },
        // { id: '2', size: 3, orientation: 'horizontal' },
        // { id: '3', size: 3, orientation: 'horizontal' },
    ];
    const { sockets } = useWebSocket();
    const socket = sockets[lobbyCode];

    const [currentShipIndex, setCurrentShipIndex] = useState(0);
    const [placedShips, setPlacedShips] = useState([]);
    const [board, setBoard] = useState(Array(10).fill(null).map(() => Array(10).fill(". ")));
    const [currentOrientation, setCurrentOrientation] = useState('horizontal');
    const [tentativePlacement, setTentativePlacement] = useState(false);
    const [loading, setLoading] = useState(false);

    const addShip = (x, y, length, direction, shipId) => {
        if (socket) {

            let orientation;
            if (direction === 'horizontal') {
                orientation = 'east';
            } else {
                orientation = 'south';
            }

            const message = JSON.stringify({
                type: 'ADD_SHIP',
                x,
                y,
                length,
                orientation,
                shipId,
            });
            socket.send(message);
        }
    };


    const handleCellClick = (row, col) => {
        const currentShip = ships[currentShipIndex];
        if (currentShip) {
            const newBoard = [...board];
            const { size } = currentShip;

            // Clear previous tentative placement
            if (tentativePlacement) {
                const { row: prevRow, col: prevCol } = tentativePlacement;
                for (let i = 0; i < size; i++) {
                    if (tentativePlacement.orientation === 'horizontal') {
                        newBoard[prevRow][prevCol + i] = '. ';
                    } else {
                        newBoard[prevRow + i][prevCol] = '. ';
                    }
                }
            }

            if (currentOrientation === 'horizontal' && col + size > 10) {
                console.log("ERROR: Ship does not fit horizontally");
                return;
            }
            if (currentOrientation === 'vertical' && row + size > 10) {
                console.log("ERROR: Ship does not fit vertically");
                return;
            }

            // Check for ship collisions
            for (let i = 0; i < size; i++) {
                if (currentOrientation === 'horizontal' && newBoard[row][col + i] !== '. ') {
                    console.log("ERROR: Ship collision detected");
                    return;
                }
                if (currentOrientation === 'vertical' && newBoard[row + i][col] !== '. ') {
                    console.log("ERROR: Ship collision detected");
                    return;
                }
            }

            // Tentatively place the ship on the board using currentOrientation
            for (let i = 0; i < size; i++) {
                if (currentOrientation === 'horizontal') {
                    newBoard[row][col + i] = `TB${currentShip.id}`;
                } else {
                    newBoard[row + i][col] = `TB${currentShip.id}`;
                }
            }

            setTentativePlacement({ row, col, newBoard, orientation: currentOrientation });
        }
    };


    async function populatePlayerBoard(lobbyCode, sessionId, playerBoard) {
        const url = `https://${import.meta.env.VITE_WEBSOCKET_URL}/lobby/${lobbyCode}/populatePlayerBoard`;
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                sessionId: sessionId,
                playerBoard: playerBoard
            })
        });

        if (!response.ok) {
            throw new Error('Failed to populate player board');
        }
    }
    async function updateAreShipsPlaced(lobbyCode, sessionId) {
        const url = `https://${import.meta.env.VITE_WEBSOCKET_URL}/lobby/${lobbyCode}/updateAreShipsPlaced`;
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                sessionId: sessionId,
                areShipsPlaced: true
            })
        });

        if (!response.ok) {
            throw new Error('Failed to update areShipsPlaced');
        }
    }

    const confirmShipPlacement = () => {
        if (tentativePlacement) {
            const { row, col, newBoard, orientation } = tentativePlacement;
            const currentShip = ships[currentShipIndex];
            const { size } = currentShip;
            for (let i = 0; i < size; i++) {
                if (orientation === 'horizontal') {
                    newBoard[row][col + i] = `B${currentShip.id}`;
                } else {
                    newBoard[row + i][col] = `B${currentShip.id}`;
                }
            }

            setBoard(newBoard);
            setPlacedShips([...placedShips, { ...currentShip, row, col }]);
            setCurrentShipIndex(currentShipIndex + 1);
            addShip(col, row, size, orientation, currentShip.id);
            setTentativePlacement(null);
        }
    };

    useEffect(() => {
        const handleKeyPress = (event) => {
            if (event.key === 'r' || event.key === 'R') {
                handleRotate();
            }
            if (event.key === 'Enter') {
                confirmShipPlacement();
            }
        };

        window.addEventListener('keydown', handleKeyPress);
        return () => {
            window.removeEventListener('keydown', handleKeyPress);
        };
    }, [currentShipIndex]);

    const handleRotate = () => {
        const currentShip = ships[currentShipIndex];
        if (currentShip) {
            const newOrientation = currentShip.orientation === 'horizontal' ? 'vertical' : 'horizontal';
            setCurrentOrientation(newOrientation);
            ships[currentShipIndex] = { ...currentShip, orientation: newOrientation };
            console.log(`Rotated ship to ${newOrientation}`);
        }
    };

    //TODO: will get a message from the server to state that other players are ready
    //TODO: Eventually once all players confirm their ship placements, the server will send a message to navigate to the gameplay page
    const handleGoToGame = () => {
        setLoading(true);
        populatePlayerBoard(getCookie('lobbyCode'),sessionId, board);
        updateAreShipsPlaced(getCookie('lobbyCode'), sessionId);
        // console.log(board);
        setTimeout(() => {
        }, 2000);
    };

    return (
        <div className={'container'}>
                <div className={'players-container'}>
                    <PlayerTag displayName={displayName} color={color}/>
                </div>
            {
                currentShipIndex < ships.length &&
                <h3 className={'placing-cell-text'}>You are placing a {ships[currentShipIndex].size} cell ship.</h3>}
                <div className={'boards-container'}>
                    <div className={'client-board'}>
                        <BattleShipBoard board={tentativePlacement ? tentativePlacement.newBoard : board} onClick={handleCellClick} />
                        {currentShipIndex < ships.length ? (
                            <div className="ship-controls">
                                <p>Your current ship orientation is <span style={{fontWeight: "bold"}}>{currentOrientation}</span> press &apos;R&apos; to Change</p>
                                    <button className={'ship-placement-button'} onClick={confirmShipPlacement} disabled={!tentativePlacement} >Confirm Ship</button>
                            </div>
                        ) : (
                            <div className="ship-controls">
                                <button className={'ship-placement-button'} onClick={handleGoToGame}
                                        disabled={loading}>
                                    Go to Game
                                    {loading && <span className="loading-circle"></span>}
                                </button>
                                {loading && <p className="waiting-text"> Waiting for other players...</p>}
                            </div>
                        )}
                    </div>
                </div>
        </div>
    );
};

ShipPlacementPanel.propTypes = {
    sessionId: PropTypes.string.isRequired,
    displayName: PropTypes.string.isRequired,
    lobbyCode: PropTypes.string.isRequired,
    color: PropTypes.string.isRequired,
};

export default ShipPlacementPanel;
