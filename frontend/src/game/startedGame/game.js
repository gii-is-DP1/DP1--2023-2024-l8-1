
import { useState } from "react";
import "./styles.css"


function Sector({ position, squares, handleClick }) {
    return (
        <>
            <div className="board-row">
                <Square value={squares[9 * position + 0]} onSquareClick={() => handleClick(position, 9 * position + 0)} />
                <Square value={squares[9 * position + 1]} onSquareClick={() => handleClick(position, 9 * position + 1)} />
                <Square value={squares[9 * position + 2]} onSquareClick={() => handleClick(position, 9 * position + 2)} />
            </div>
            <div className="board-row">
                <Square value={squares[9 * position + 3]} onSquareClick={() => handleClick(position, 9 * position + 3)} />
                <Square value={squares[9 * position + 4]} onSquareClick={() => handleClick(position, 9 * position + 4)} />
                <Square value={squares[9 * position + 5]} onSquareClick={() => handleClick(position, 9 * position + 5)} />
            </div>
            <div className="board-row">
                <Square value={squares[9 * position + 6]} onSquareClick={() => handleClick(position, 9 * position + 6)} />
                <Square value={squares[9 * position + 7]} onSquareClick={() => handleClick(position, 9 * position + 7)} />
                <Square value={squares[9 * position + 8]} onSquareClick={() => handleClick(position, 9 * position + 8)} />
            </div>
        </>
    )
}

function Square({ value, onSquareClick }) {
    return (
        <button className="square" onClick={onSquareClick}>
            {value}
        </button>
    );
}

export default function Board() {
    const [players, setPlayers] = useState(['X', 'O', 'Y']);
    const [currentPlayerIndex, setCurrentPlayerIndex] = useState(0);
    const [lap, setLap] = useState(0);
    const [turn, setTurn] = useState(0);
    //const [sector, setSector] = useState(Array(3).fill(null));
    const [squares, setSquares] = useState(Array(54).fill(null));

    function handleClick(position, i) {
        if (squares[i]) {
            return;
        }
        let occuped = false;

        for (let j = 0; j < 9; j++) {
            if (squares[position * 9 + j] !== null) return occuped = true;
        }

        if (turn === 0) {
            if (occuped) {
                console.log(lap);
            } else {
                const nextSquares = squares.slice();

                nextSquares[i] = players[currentPlayerIndex];

                setSquares(nextSquares);


                if (lap === 0) {
                    setCurrentPlayerIndex(currentPlayerIndex + 1 === 3 ? 2 : currentPlayerIndex + 1);
                    console.log(currentPlayerIndex);
                    setLap(currentPlayerIndex === 2 ? 1 : 0);
                    console.log(lap);
                } else if (lap === 1) {
                    setCurrentPlayerIndex(currentPlayerIndex - 1 === -1 ? 0 : currentPlayerIndex - 1);
                    console.log(currentPlayerIndex);
                    setTurn(currentPlayerIndex === 0 ? 1 : 0);
                    console.log(lap);
                }
            }

        }

    }

    return (
        <>
            <div className="board-row">
                <Sector position={0} squares={squares} handleClick={handleClick} />
                <Sector position={1} squares={squares} handleClick={handleClick} />
                <Sector position={2} squares={squares} handleClick={handleClick} />
            </div>
            <div className="board-row">
                <Sector position={3} squares={squares} handleClick={handleClick} />
                <Sector position={4} squares={squares} handleClick={handleClick} />
                <Sector position={5} squares={squares} handleClick={handleClick} />
            </div>
        </>
    );
}
