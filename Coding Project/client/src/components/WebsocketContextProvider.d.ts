declare module '../WebsocketContextProvider.jsx' {
    import { ReactNode } from 'react';

    interface WebSocketProviderProps {
        children: ReactNode;
        setSessionId: (sessionId: string) => void;
    }

    export const WebSocketProvider: (props: WebSocketProviderProps) => JSX.Element;
    export const useWebSocket: () => {
        sockets: Record<string, WebSocket>;
        connectToLobby: (lobbyCode: string) => void;
        disconnectFromLobby: (lobbyCode: string) => void;
    };
}