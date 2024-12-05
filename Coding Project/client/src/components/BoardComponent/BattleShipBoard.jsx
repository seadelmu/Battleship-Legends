import PropTypes from 'prop-types';
import './BattleShipBoard.css';

const BattleShipBoard = ({ board, onClick }) => {

    const player1Color = '#9120fa';
    const player2Color = '#FFD700';
    const player3Color = '#1baeed';
    const player4Color = '#07ff00';


    return (
        <>
            <div className={'top-letter-coord'}>
                <div className={'letters'}>
                    <div className={'letter-coord'}>A</div>
                    <div className={'letter-coord'}>B</div>
                    <div className={'letter-coord'}>C</div>
                    <div className={'letter-coord'}>D</div>
                    <div className={'letter-coord'}>E</div>
                    <div className={'letter-coord'}>F</div>
                    <div className={'letter-coord'}>G</div>
                    <div className={'letter-coord'}>H</div>
                    <div className={'letter-coord'}>I</div>
                    <div className={'letter-coord'}>J</div>
                </div>
                <div className={'left-number-coord'}>
                    <div className={'numbers'}>
                        <div className={'number-coord'}>1</div>
                        <div className={'number-coord'}>2</div>
                        <div className={'number-coord'}>3</div>
                        <div className={'number-coord'}>4</div>
                        <div className={'number-coord'}>5</div>
                        <div className={'number-coord'}>6</div>
                        <div className={'number-coord'}>7</div>
                        <div className={'number-coord'}>8</div>
                        <div className={'number-coord'}>9</div>
                        <div className={'number-coord'}>10</div>
                    </div>
                    <div className="board">
                        {board.map((row, i) => (
                            row.map((cell, j) => {
                                const isTestBoatCell = /^TB\d+$/.test(cell);
                                const isRealBoatCell = /^B\d+$/.test(cell);

                                // TODO Add power ups visible on player board?
                                const isMissCell_1 = cell === 'M_' + player1Color ; // M_#FFD700
                                const isMissCell_2 = cell === 'M_' + player2Color ; // M_#FFD700
                                const isMissCell_3 = cell === 'M_' + player3Color ; // M_#FFD700
                                const isMissCell_4 = cell === 'M_' + player4Color ; // M_#FFD700

                                const isHitCell_1 = cell === 'H_' + player1Color ; // H_#FFD700
                                const isHitCell_2 = cell === 'H_' + player2Color ; // H_#FFD700
                                const isHitCell_3 = cell === 'H_' + player3Color ; // H_#FFD700
                                const isHitCell_4 = cell === 'H_' + player4Color ; // H_#FFD700

                                const isBoatCellHit_1 = /^B\d+H_#9120fa$/.test(cell); // B\d+H_#9120fa
                                const isBoatCellHit_2 = /^B\d+H_#FFD700$/.test(cell); // B\d+H_#FFD700
                                const isBoatCellHit_3 = /^B\d+H_#1baeed$/.test(cell); // B\d+H_#1bed8e
                                const isBoatCellHit_4 = /^B\d+H_#07ff00$/.test(cell); // B\d+H_#f19002
                                const isBoatCellPointCell =  /^P$/.test(cell)

                                const isPointCellHit_1 = /^P_HIT_#9120fa$/.test(cell); // B\d+H_#9120fa
                                const isPointCellHit_2 = /^P_HIT_#FFD700$/.test(cell); // B\d+H_#FFD700
                                const isPointCellHit_3 = /^P_HIT_#1baeed$/.test(cell); // B\d+H_#1bed8e
                                const isPointCellHit_4 = /^P_HIT_#07ff00$/.test(cell); // B\d+H_#f19002
                                return (

                                    // This is hard to look at....
                                    <div
                                        key={`${i}-${j}`}
                                        className={`cell ${isTestBoatCell ? 'tentative-boat-cell' : ''}
                                            ${isRealBoatCell ? 'real-boat-cell' : ''}
                                            ${isMissCell_1 ? 'miss-cell-1' : ''}
                                            ${isMissCell_2 ? 'miss-cell-2' : ''}
                                            ${isMissCell_3 ? 'miss-cell-3' : ''}
                                            ${isMissCell_4 ? 'miss-cell-4' : ''}
                                            ${isHitCell_1 ? 'hit-cell-1' : ''}
                                            ${isHitCell_2 ? 'hit-cell-2' : ''}
                                            ${isHitCell_3 ? 'hit-cell-3' : ''}
                                            ${isHitCell_4 ? 'hit-cell-4' : ''}
                                            ${isBoatCellHit_1 ? 'boat-cell-hit-1' : ''}
                                            ${isBoatCellHit_2 ? 'boat-cell-hit-2' : ''}
                                            ${isBoatCellHit_3 ? 'boat-cell-hit-3' : ''}
                                            ${isBoatCellHit_4 ? 'boat-cell-hit-4' : ''}
                                            ${isBoatCellPointCell ? 'point-cell': ''}
                                            ${isPointCellHit_1 ? 'point-cell-hit-1': ''}
                                            ${isPointCellHit_2 ? 'point-cell-hit-2': ''}
                                            ${isPointCellHit_3 ? 'point-cell-hit-3': ''}
                                            ${isPointCellHit_4 ? 'point-cell-hit-4': ''}`}
                                        onClick={() => onClick(i, j)}
                                    />
                                );
                            })
                        ))}
                    </div>
                </div>
            </div>
        </>
    );
}

BattleShipBoard.propTypes = {
    board: PropTypes.arrayOf(PropTypes.arrayOf(PropTypes.string)).isRequired,
    onClick: PropTypes.func.isRequired,
};

export default BattleShipBoard;