import './EnterLobbyPage.css';
import { useNavigate } from "react-router-dom";
import { useWebSocket } from "../../components/WebsocketContextProvider.jsx";
import { useEffect, useState } from "react";
import Toast from "../../components/Toast/Toast.jsx";
import FormValidation from "../../components/FormValidationComponent/FormValidation";

function doesLobbyExist(lobbyCode, navigate, connectToLobby) {
    if (!lobbyCode) {
        console.error('Lobby code is not set');
        return false;
    }

    let exists = false;
    console.log('New LobbyCode: ' + lobbyCode);
    fetch(`http://localhost:8080/lobby/doesLobbyExist/${lobbyCode}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include', // Include credentials if needed
    })
        .then(response => response.json())
        .then(data => {
            exists = data;
            if (exists) {
                console.log("Lobby exists");
                connectToLobby(lobbyCode);
                sendAMessageToServer(lobbyCode, navigate);
            } else {
                console.log("Lobby does not exist");
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
    return exists;
}

function storeLobbyCodeCookie(lobbyCode) {
    localStorage.setItem('lobbyCode', lobbyCode);
}

function sendAMessageToServer(lobbyCode, navigate) {
    console.log("Sending a message to the server to join the lobby: " + lobbyCode);
    navigate('/display-name');
}

function check_lobbyCode(lobbyCode) {
    const str_size = lobbyCode.length;
    if (str_size < 1) {
        return false;
    }

    for (let i = 0; i < str_size; i++) {
        let character_code = lobbyCode.charCodeAt(i);
        if (character_code < 48 || character_code > 57) { // check ascii values to see if it's not an integer
            return false;
        }
    }
    return true; // checked all characters return true
}

const EnterLobbyPage = () => {
    const [lobbyCode, setLobbyCode] = useState('');
    const [valid_lobbyCode, setLobbyValidity] = useState(true);
    const navigate = useNavigate();
    const { connectToLobby } = useWebSocket();
    const [toastVisible, setToastVisible] = useState(false);
    const [toastType, setToastType] = useState("success");
    const [toastMessage, setToastMessage] = useState("");
    const [buttonDisabled, setButtonDisabled] = useState(false);

    const showToast = (type) => {
        setToastType(type);
        setToastVisible(true);
        setTimeout(() => {
            setToastVisible(false);
        }, 3000); // Hide toast after 3 seconds
    };

    const lobbyCodeOperations = (lobbyCode, navigate, connectToLobby) => {
        console.log(lobbyCode);
        storeLobbyCodeCookie(lobbyCode);
        if (!doesLobbyExist(lobbyCode, navigate, connectToLobby)) {
            setToastMessage("Lobby does not exist");
            showToast("error");
        }
    }

    const handleSubmit = (event) => {
        event.preventDefault();
        setLobbyValidity(check_lobbyCode(lobbyCode));
        if (check_lobbyCode(lobbyCode)) {
            lobbyCodeOperations(lobbyCode, navigate, connectToLobby);
        } else {
            showToast("error");
            setToastMessage("Invalid Format: Enter numbers only");
        }
    };

    useEffect(() => {
        if (lobbyCode) {
            console.log('New LobbyCode:', lobbyCode);
        }
    }, [lobbyCode]);

    const handleCreateLobby = () => {
        storeLobbyCodeCookie(lobbyCode);
        if (!doesLobbyExist(lobbyCode, navigate, connectToLobby)) {
            setToastMessage("Lobby does not exist");
            showToast("error");
        }
    };

    async function createLobby(password, maxPlayers, lobbyCode) {
        try {
            const response = await fetch(`http://localhost:8080/lobby/createLobby?password=${password}&maxPlayers=${maxPlayers}&lobbyCode=${lobbyCode}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include', // Include credentials if needed
            });
            const data = await response.json();
            console.log("LobbyCode from server: " + data.lobbyCode);
            setLobbyCode(data.lobbyCode);
            await handleCreateLobby(); // Ensure this runs after createLobby
        } catch (error) {
            console.error('Error:', error);
        }
    }

    function generateLobbyCode() {
        return Math.floor(1000 + Math.random() * 9000).toString();
    }

    return (
        <div className="container">
            <section className="section">
                <img src="/bote.png" alt="Description of image" className={'boat-image'} />
                <h1 className="heading">BattleShip</h1>
                <Toast visible={toastVisible} message={toastMessage} type={toastType} />
                <form className="form" onSubmit={handleSubmit}>
                    <FormValidation
                        placeholder="Enter a numeric lobby code"
                        errorMessage="Enter numbers only"
                        visible={valid_lobbyCode}
                        onChange={(event) => {
                            setLobbyCode(event.target.value);
                            setLobbyValidity(check_lobbyCode(event.target.value));
                        }}
                    />
                    <button
                        disabled={false}
                        type="submit"
                        className="join-button"
                        value="Join Lobby"
                        onClick={(e) => {
                            handleSubmit(e);
                        }}
                    >Join Lobby
                    </button>
                </form>
                <button
                    type="reset"
                    className="create-button"
                    onClick={async (event) => {
                        event.preventDefault();
                        const newLobbyCode = generateLobbyCode();
                        setLobbyCode(newLobbyCode);
                        setButtonDisabled(true); // Disable the button
                        try {
                            await createLobby("", 4, newLobbyCode);
                        } catch (error) {
                            console.error('Error:', error);
                        } finally {
                            setButtonDisabled(false); // Re-enable the button once done
                        }
                    }}
                >
                    Create Lobby
                </button>
                <button
                    type="button"
                    id="help-button"
                    onClick={() => navigate('/help')}
                >
                    Help
                </button>
            </section>
        </div>
    );
}

export default EnterLobbyPage;