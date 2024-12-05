import {BrowserRouter as Router, Navigate, Route, Routes} from 'react-router-dom';
import EnterLobbyPage from './pages/EnterLobbyPage/EnterLobbyPage.jsx';
import PlayPage from "./pages/PlayPage/PlayPage.jsx";
import HelpPage from "./pages/HelpPage/HelpPage.tsx";
import './App.css';
import EnterDisplayName from "./pages/EnterDisplayNamePage/EnterDisplayName.jsx";
import {WebSocketProvider} from "./components/WebsocketContextProvider.jsx";
import {useState} from "react";


function App() {
    const [sessionId, setSessionId] = useState('');

  return (
      <WebSocketProvider setSessionId={setSessionId}>
      <Router>
          <Routes>
              <Route path="/" element={<Navigate to="/entry" />} />
              <Route path={'/entry'} exact element={<EnterLobbyPage/>}/>
              <Route path={'/display-name'} exact element={<EnterDisplayName/>}/>
              <Route path={'/play'} element={<PlayPage sessionId={sessionId}/>}/>
              <Route path={'/help'} element={<HelpPage/>}/>
              {/*<Route path={'/place-ships'} element={<ShipPlacement/>}/>*/}
              {/*<Route path={'/gameplay'} element={<GameplayPage/>}/>*/}
              {/*pass in what lobby we are in here to hop websocket to websocket*/}
              {/*<Route path={'/websocket'} element={<WebSocket lobbyCode={'1'}/>}/>*/}
          </Routes>
      </Router>
        </WebSocketProvider>

  )
}

export default App
