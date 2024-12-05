import React from "react";
import './ChatMessage.css';

interface Props{
    key: string;
    displayName: string;
    displayNameColor: string;
    messageContents: string;
}

const ChatMessage: React.FC<Props> = ({key, displayName, displayNameColor, messageContents}) => {
    return (
        <div id="chat-message-container" key={key}>
            <p id="display-name" style={{color: displayNameColor}}>{displayName}</p>
            <p id="message-contents">{messageContents}</p>
        </div>
    )
}

export default ChatMessage;