import './GameHint.css';
import PropTypes from 'prop-types';

const GameHint = ({ message, visible }) => {
    return (
        <div className={`gamehint ${visible ? 'visible' : ''}`}>
            {message}
        </div>
    );
};

GameHint.propTypes = {
    message: PropTypes.string.isRequired,
    visible: PropTypes.bool.isRequired,
};

export default GameHint;