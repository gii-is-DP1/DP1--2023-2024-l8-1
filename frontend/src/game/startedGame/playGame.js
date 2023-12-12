import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchState from "../../util/useFetchState";
import tokenService from "../../services/token.service";
import { useState } from "react";
import "./tablero.css"

const jwt = tokenService.getLocalAccessToken();


function Sector({ position, hexes, handleClick }) {
    let puntos = hexes.map((x) => x[1])
    return (
        <>
            <div className="board-row">
                <Hex value={puntos[0]} onhexeClick={() => handleClick(position, 9 * position + 0)} />
                <Hex value={puntos[1]} onhexeClick={() => handleClick(position, 9 * position + 1)} />
            </div>
            <div className="board-row">
                <Hex value={puntos[2]} onhexeClick={() => handleClick(position, 9 * position + 3)} />
                <Hex value={puntos[3]} onhexeClick={() => handleClick(position, 9 * position + 4)} />
                <Hex value={puntos[4]} onhexeClick={() => handleClick(position, 9 * position + 5)} />
            </div>
            <div className="board-row">
                <Hex value={puntos[5]} onhexeClick={() => handleClick(position, 9 * position + 6)} />
                <Hex value={puntos[6]} onhexeClick={() => handleClick(position, 9 * position + 7)} />
            </div>
        </>
    )
}

function TriPrime({ position, hex, handleClick }) {
    return (
        <div className="sector-container">
            <Hex value={hex} onhexeClick={() => handleClick(position, 9 * position + 6)} />
        </div>
    );
}


function Hex({ value, onhexeClick }) {
    return (
        <button className="hex" onClick={onhexeClick}>
            {value}
        </button>
    );
}

export default function PlayGame() {
    const name = getIdFromUrl(3);
    const [players, setPlayers] = useState(['X', 'O', 'Y']);
    const [currentPlayerIndex, setCurrentPlayerIndex] = useState(0);
    const [lap, setLap] = useState(0);
    const [turn, setTurn] = useState(0);
    const [hexes, setHexes] = useFetchState(
        [],
        `/api/v1/gameBoard/${name}`,
        jwt
    );

    const hexList =
        hexes.map((h) => {
            const newHex = [h.id, h.puntos, h.occuped, h.position]
            return (newHex)
        })

    function handleClick(position, i) {
        if (hexList[i]) {
            return;
        }
        let occuped = false;

        for (let j = 0; j < 9; j++) {
            if (hexList[position * 9 + j] !== null) return occuped = true;
        }

        if (turn === 0) {
            if (occuped) {
                console.log(lap);
            } else {
                const nexthexes = hexList.slice();

                nexthexes[i] = players[currentPlayerIndex];

                setHexes(nexthexes);


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
        <div className="game">
            <div className="center-container">
                <div className="left-sector">
                    <Sector position={0} hexes={hexList.slice(14, 21)} handleClick={handleClick} />
                </div>
                <div className="right-sector">
                    <Sector position={1} hexes={hexList.slice(14, 21)} handleClick={handleClick} />
                </div>
            </div>
            <div className="center-container">
                <div className="left-sector">
                    <Sector position={2} hexes={hexList.slice(14, 21)} handleClick={handleClick} />
                </div>
                <div className="tri-prime-container">
                    <TriPrime position={3} hex={hexList[42]} handleClick={handleClick} />
                </div>
                <div className="right-sector">
                    <Sector position={4} hexes={hexList.slice(21, 28)} handleClick={handleClick} />
                </div>
            </div>
            <div className="center-container">
                <div className="left-sector">
                    <Sector position={5} hexes={hexList.slice(14, 21)} handleClick={handleClick} />
                </div>
                <div className="right-sector">
                    <Sector position={6} hexes={hexList.slice(14, 21)} handleClick={handleClick} />
                </div>
            </div>
        </div>
    );
}