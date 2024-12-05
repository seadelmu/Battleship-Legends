import PropTypes from 'prop-types';
import './PlayerTag.css'
const PlayerTag = (props) => {

    const hexToRgba = (hex, alpha) => {
        const r = parseInt(hex.slice(1, 3), 16);
        const g = parseInt(hex.slice(3, 5), 16);
        const b = parseInt(hex.slice(5, 7), 16);
        return `rgba(${r}, ${g}, ${b}, ${alpha})`;
    };

    const backgroundColor = hexToRgba(props.color, 0.35); // 50%
    return (
        <div className={'name-holder'} style={{backgroundColor: backgroundColor, border: `1.5px solid ${props.color}`}}>
            <div className={'player-name-text'}>{props.displayName}</div>

        </div>
    );
};

PlayerTag.propTypes = {
    displayName: PropTypes.string.isRequired,
    color: PropTypes.string
};

export default PlayerTag;