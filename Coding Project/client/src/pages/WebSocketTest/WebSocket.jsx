import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';

const WebSocket = ({ lobbyCode }) => {
    const [connectedClients, setConnectedClients] = useState(0);
    const [socket, setSocket] = useState(null);

    useEffect(() => {
        const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
        const ws = new window.WebSocket(`${protocol}://${import.meta.env.VITE_WEBSOCKET_URL}/${protocol}/${lobbyCode}`);

        ws.onopen = () => {
            console.log('WebSocket connection established');
        };

        ws.onmessage = (event) => {
            console.log('Message from server:', event.data);
        };

        ws.onclose = () => {
            console.log('WebSocket connection closed');
        };

        setSocket(ws);

        // Fetch the number of connected clients from a specific lobby code
        fetch(`/${protocol}/${lobbyCode}`)
            .then(response => response.json())
            .then(data => setConnectedClients(data));

        return () => {
            ws.close();
        };
    }, [lobbyCode]);

    return (
        <div>
            <h1>WebSocket Example</h1>
            <p>Connected Clients: {connectedClients}</p>
        </div>
    );
};

WebSocket.propTypes = {
    lobbyCode: PropTypes.string.isRequired,
};

export default WebSocket;