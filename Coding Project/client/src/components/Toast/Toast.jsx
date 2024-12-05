import './Toast.css';
import PropTypes from "prop-types";



const Toast = ({ message, type, visible }) => {
    return (
        <div className={`toast ${type} ${visible ? 'visible' : ''}`}>
            {message}
        </div>
    );
};

Toast.propTypes = {
    message: PropTypes.string.isRequired,
    type: PropTypes.string.isRequired,
    visible: PropTypes.bool.isRequired,
}

export default Toast;