import  { createContext, useContext, useState, useEffect } from 'react';
import PropTypes from "prop-types";

const WebSocketContext = createContext(null);

export const WebSocketProvider = ({ children, setSessionId}) => {
    const [sockets, setSockets] = useState({});

    const connectToLobby = (lobbyCode) => {
        if (!sockets[lobbyCode]) {
            const ws = new window.WebSocket(`ws://localhost:8080/ws/${lobbyCode}`);
            console.log('New lobby created');
            ws.onmessage = function(event) {
                const data = JSON.parse(event.data);
                if (data.sessionId) {
                    console.log('Session ID:', data.sessionId);
                    setSessionId(data.sessionId);

                    document.cookie = `lobbyCode=${lobbyCode}; path=/`;
                    document.cookie = `sessionId=${data.sessionId}; path=/`;

                }
            };

            ws.onopen = () => {
                console.log(`WebSocket connection established for lobby ${lobbyCode}`);
            };

            ws.onclose = () => {
                console.log(`WebSocket connection closed for lobby ${lobbyCode}`);
                setSockets((prevSockets) => {
                    const updatedSockets = { ...prevSockets };
                    delete updatedSockets[lobbyCode];
                    return updatedSockets;
                });
            };

            setSockets((prevSockets) => ({
                ...prevSockets,
                [lobbyCode]: ws,
            }));
        }
    };

    const disconnectFromLobby = (lobbyCode) => {
        if (sockets[lobbyCode]) {
            sockets[lobbyCode].close();
        }
    };

    useEffect(() => {
        return () => {
            Object.values(sockets).forEach((socket) => socket.close());
        };
    }, [sockets]);

    return (
        <WebSocketContext.Provider value={{ sockets, connectToLobby, disconnectFromLobby }}>
            {children}
        </WebSocketContext.Provider>
    );
};

export const useWebSocket = () => useContext(WebSocketContext);

WebSocketProvider.propTypes = {
    children: PropTypes.node.isRequired,
    setSessionId: PropTypes.func.isRequired,
};
