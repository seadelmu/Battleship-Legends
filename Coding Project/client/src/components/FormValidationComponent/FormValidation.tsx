import React, {useState, ChangeEventHandler} from 'react';
import './FormValidation.css';

interface Props{
    value: string;
    placeholder: string; //placeholder for the text
    errorMessage: string; //error message for the text
    visible: boolean; //boolean value to check if the error message is visible
    onChange: ChangeEventHandler<HTMLInputElement> //function to handle the change event
}

const FormValidation: React.FC<Props> = ({value, placeholder, errorMessage, visible, onChange}) => {
    const [inputValue, setInputValue] = useState(value);
    const [isVisible, setIsVisible] = useState(visible);
    const [error, setError] = useState(errorMessage);

    return(
        <div className="form-container">
            <input type="text" placeholder={placeholder} value={value} onChange={onChange} className="form-input"/>
            {!visible ? <p className="invalid-input">{errorMessage}</p> : <p className="no-visibility">invisible</p>}
        </div>
    );
};

export default FormValidation;