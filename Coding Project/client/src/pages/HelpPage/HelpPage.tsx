import './HelpPage.css'
import React from 'react';
import {useNavigate} from "react-router-dom";

const HelpPage = () => {
    const navigate = useNavigate();

    return (
        <div id="tutorial-container">
          <h1 style={{marginTop: 0, alignSelf: "center"}}>Tutorial</h1>
            <h2>Joining/Creating a Lobby</h2>
            <p className="article-paragraph">
                At the entry page there will be a field where you would enter a lobby code.
                This lobby code must be a numeric number. If the lobby doesn't exist then the
                user should create a lobby code so that others can use it to join.
            </p>
            <h2>Joining a Match</h2>
            <p className="article-paragraph">
                Once you have joined the lobby, you will be required to enter a display name that
                other users can see. The display name can only contain letters, numbers, and underscores.
                If you wish, you may click on the randomize button to help choosing a display name easier.
                Once you have chosen your name, you will be put into a lobby where you can see other players
                and a chat. There must be 4 players in the lobby and they must be ready in order for the match
                to start. While waiting, you may chat with other players in the lobby. Note that messages
                are not stored, if you refresh the page the messages will be lost.
            </p>
            <h2>Playing the game</h2>
            <p>
                There can only be a maximum of 4 players in the match. You will see your board and the opponent's board.
                Since there will be 3 other opponents for you to attack, you will have to switch to your desired opponent's board.
                Then you will have the option to choose a cell to attack an opponent. Keep in mind you have a 30 second time window to perform an action.
                Hitting opponents or random point cells will give you points to use for the point shop. The point shop will contain power-ups for purchasing.
                These power-ups are sonar, bomb, nuke, and decoy. Once all opponents' ships are destroyed then you are considered as a victor of the game.
                However, if all of your ships are destroyed before anybody else, you will be defeated and will return to the lobby.
            </p>
          <button
              className="return-lobby-btn"
              type="button"
              onClick={() => navigate('/entry')}>
              Back to home page
          </button>
        </div>
    );
};

export default HelpPage;