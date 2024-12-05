import './EnterDisplayNamePage.css'
import {useState} from "react";
import { useNavigate } from 'react-router-dom';
import {v4 as uuidv4} from 'uuid';
import {getCookie} from "../../../utils/cookies.js";
import PropTypes from "prop-types";
import FormValidation from "../../components/FormValidationComponent/FormValidation.tsx";

const EnterDisplayName = () => {

    const navigate = useNavigate();

    const adjectives = ['Wild',
        'Small',
        'Brave',
        'Clever',
        'Swift',
        'Scared',
        'Fierce',
        'Loyal',
        'Mighty',
        'Sly',
        'Wise',
    ];
    const nouns = [
        'Boar',
        'Snail',
        'Lion',
        'Fox',
        'Hawk',
        'Snake',
        'Cat',
        'Dog',
        'Tiger',
        'Walrus'
    ];

    function generateRandomName(){
        const randomAdjective = adjectives[Math.floor(Math.random() * adjectives.length)]
        const randomNoun = nouns[Math.floor(Math.random()*nouns.length)]
        return `${randomAdjective}${randomNoun}`
    }


    const [displayName, setDisplayName] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [validDisplayName, setValidDisplayName] = useState(true);


    function handleSubmit(displayName, navigate) {
        document.cookie = `displayName=${displayName}`;
        const sessionId = getCookie('sessionId');
        const player = {displayName: displayName, id: sessionId};


        fetch(`http://localhost:8080/lobby/${getCookie('lobbyCode')}/addPlayer`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(player)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text(); // Get response as text
            })
            .then(text => {
                const data = text ? JSON.parse(text) : {}; // Parse text if not empty
                console.log('Player created:', data);
                navigate('/play');
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }

    function checkDisplayName(displayName){
        const str_size = displayName.length;
        if(str_size < 1){
            setErrorMessage('Empty field not allowed');
            return false;
        }
        if(str_size > 20){
            setErrorMessage('20 characters max');
            return false;
        }
        for(let i = 0; i < str_size; i++){
            let character_code = displayName.charCodeAt(i);
            if(!(character_code >= 48 && character_code <= 57) && !(character_code >= 65 && character_code <= 90) && !(character_code >= 97 && character_code <= 122) && !(character_code === 95)){
                setErrorMessage('Letters, numbers, and underscores only');
                return false;
            }
        }
        return true;
    }

    return (
        <div className={'container'}>
            <section className={'section'}>
                <h1>Enter Display Name</h1>
                    <FormValidation
                        errorMessage={errorMessage}
                        visible={validDisplayName}
                        placeholder={"Enter your display name"}
                        value={displayName}
                        onChange={(e) => {
                            setDisplayName(e.target.value);
                            setValidDisplayName(checkDisplayName(e.target.value));
                        }}
                    />
                    <button className={'randomize-button'}
                            type="button"
                            onClick={() => {setDisplayName(generateRandomName()); setValidDisplayName(true)}}

                    >
                        Random
                    </button>
                    <button className={'join-button'}
                        type="submit"
                            onClick={() => {
                                (checkDisplayName(displayName) && handleSubmit(displayName, navigate));
                            }}
                    >
                        Submit
                    </button>
            </section>
        </div>
    )
}

export default EnterDisplayName;

