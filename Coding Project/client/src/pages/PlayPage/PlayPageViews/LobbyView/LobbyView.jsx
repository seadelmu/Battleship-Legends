import './LobbyView.css';
import PropTypes from "prop-types";
import {getCookie} from "../../../../../utils/cookies.js";
import {useCallback, useEffect, useRef, useState} from "react";
import Toast from "../../../../components/Toast/Toast.jsx"
import {useNavigate} from "react-router-dom";
import ChatMessage from "../../../../components/MessageComponent/ChatMessage.tsx";
import {useWebSocket} from "../../../../components/WebsocketContextProvider.jsx";

export const LobbyView = ({ allReady, minPlayersReached, timer, players, setReady, onReadyUp, sessionId, ready }) => {
    const [toastVisible, setToastVisible] = useState(false);
    const [toastType, setToastType] = useState('success');
    const [toastMessage, setToastMessage] = useState('');
    const [userMessage, setUserMessage] = useState('');
    const navigate = useNavigate()
    const [chatMessages, setChatMessages] = useState([]);
    const messageContainerRef = useRef(null);
    const {sockets} = useWebSocket();
    const socket = sockets[getCookie('lobbyCode')];


    const addChatMessage = useCallback((chatComponent) => {
        setChatMessages((prevMessages) => [...prevMessages, chatComponent]);
    }, [setChatMessages]);


    useEffect(() => {
        if (messageContainerRef.current) {
            messageContainerRef.current.scrollTop = messageContainerRef.current.scrollHeight;
        }
    }, [chatMessages]);

    const lobbyCode = getCookie('lobbyCode');

    const showToast = (type) => {
        setToastType(type);
        setToastVisible(true);
        setTimeout(() => {
            setToastVisible(false);
        }, 3000); // Hide toast after 3 seconds
    };

    const copyToClipboard = () => {
        navigator.clipboard.writeText(lobbyCode).then(() => {
            setToastMessage('Lobby code copied to clipboard');
            showToast('success');
        }).catch(err => {
            console.error('Failed to copy: ', err);
        });
    };



    useEffect(() => {
        if (socket) {
            const handleMessage = (event) => {
                try {
                    const data = JSON.parse(event.data);
                    if (data.type === 'RECEIVE_MESSAGE') {
                        console.log('color', data.displayNameColor);
                        addChatMessage(
                            <ChatMessage
                                key={chatMessages.length}
                                displayName={data.displayName}
                                displayNameColor={data.displayNameColor}
                                messageContents={data.message}
                            />
                        );
                    }
                } catch (e) {
                    console.error('Error parsing incoming message: ', e);
                }
            };

            socket.addEventListener('message', handleMessage);

            // Cleanup function to remove the event listener
            return () => {
                socket.removeEventListener('message', handleMessage);
            };
        }
    }, [socket, chatMessages]);





    function handleSendMessage(){
        const clientDisplayName = players.find(player => player.id === sessionId).displayName;
        if(userMessage === ''){
            return;
        }
        const messageKey = `${clientDisplayName}-${chatMessages.length}`;
        addChatMessage(<ChatMessage key={messageKey} displayName={clientDisplayName} displayNameColor={players.find(player => player.id === sessionId).color} messageContents={userMessage} />);
        setUserMessage('');
        if (socket) {
            const message = JSON.stringify({ type: 'SEND_MESSAGE', message: userMessage, color: players.find(player => player.id === sessionId).color});
            socket.send(message);
        }
    }

    return (
        <div>
            <Toast message={toastMessage} type={toastType} visible={toastVisible} />
        <h3 className={'heading'}>Lobby</h3>
            <div className={'lobby-code'} onClick={copyToClipboard}>
        <p>{getCookie('lobbyCode')}</p>
    </div>
            <section className={'lobby-container'}>
                <div className={'col-one'}>
                    <div>
                        {allReady && minPlayersReached && <h1 className="timer">{timer}</h1>}
                        <div className={'playerlist-holder'}>
                            {players.map((player, index) => (
                                <div
                                    className={`player-item ${player.displayName === 'Waiting for player...' ? '' : player.ready ? 'ready-gradient' : 'not-ready-gradient'}`}
                                    key={index}
                                >
                                    {player.displayName === 'Waiting for player...' ? (
                                        <>
                                            <div className="spinner"></div>
                                            {player.displayName}
                                        </>
                                    ) : (
                                        <>
                                            {player.displayName}
                                        </>
                                    )}
                                    {player.id === sessionId && <span>&nbsp;(You)</span>}
                                </div>
                            ))}
                        </div>
                    </div>
                    <div></div>
                    <button
                        className={`ready-button ${ready ? 'unready-button' : ''}`}
                        onClick={() => {
                            setReady(!ready);
                            onReadyUp();
                        }}
                    >
                        {ready ? 'Unready' : 'Ready Up'}
                    </button>
                    <button
                        className={`leave-lobby-button ready-button`}
                        onClick={() => {
                            navigate('/')
                        }}
                    >
                        Leave Lobby
                    </button>
                </div>
                <div className="chat-container">
                    <div className="message-container" ref={messageContainerRef}>
                        {chatMessages.map((chatMessage) => (
                            chatMessage
                        ))}
                    </div>
                    <div className="chat-form-wrapper">
                        <div className="chat-form">
                            <input type="text"
                                   id="chat-input"
                                   placeholder="Type a message..."
                                   value={userMessage}
                                   onChange={(event) => {setUserMessage(event.target.value)}}
                                   onKeyDown={(event) => {if(event.key === 'Enter') handleSendMessage();}}
                            />
                            <button className="send-message-button"
                                    onClick={handleSendMessage}
                            >
                            &#9166;</button>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    );
}

LobbyView.propTypes = {
    allReady: PropTypes.bool.isRequired,
    minPlayersReached: PropTypes.bool.isRequired,
    timer: PropTypes.number.isRequired,
    players: PropTypes.arrayOf(PropTypes.shape({
        displayName: PropTypes.string.isRequired,
        ready: PropTypes.bool.isRequired,
        id: PropTypes.string
    })).isRequired,
    setReady: PropTypes.func.isRequired,
    onReadyUp: PropTypes.func.isRequired,
    sessionId: PropTypes.string,
    ready: PropTypes.bool.isRequired
};