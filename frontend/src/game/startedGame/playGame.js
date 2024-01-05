import getIdFromUrl from "../../util/getIdFromUrl";
import useIntervalFetchState from "../../util/useIntervalFetchState";
import useFetchState from "../../util/useFetchState"
import tokenService from "../../services/token.service";
import { useState } from "react";
import "./tablero.css"
import expand from "../../static/images/Expand.jpg"
import explore from "../../static/images/Explore.jpg"
import exterminate from "../../static/images/Exterminate.jpg"

const jwt = tokenService.getLocalAccessToken();


function Sector({ position, hexes, handleClick }) {
    let puntos = hexes.map((x) => x[3])
    return (
        <div className="sector-container">
            <div className="row-up">
                <Hex value={puntos[0]} onhexeClick={() => handleClick(position, 7 * position + 0)} />
                <Hex value={puntos[1]} onhexeClick={() => handleClick(position, 7 * position + 1)} />
            </div>
            <div>
                <Hex value={puntos[2]} onhexeClick={() => handleClick(position, 7 * position + 3)} />
                <Hex value={puntos[3]} onhexeClick={() => handleClick(position, 7 * position + 4)} />
                <Hex value={puntos[4]} onhexeClick={() => handleClick(position, 7 * position + 5)} />
            </div>
            <div className="row-down">
                <Hex value={puntos[5]} onhexeClick={() => handleClick(position, 7 * position + 6)} />
                <Hex value={puntos[6]} onhexeClick={() => handleClick(position, 7 * position + 7)} />
            </div>
        </div>
    )
}

function TriPrime({ position, hex, handleClick }) {
    return (
        <div className="sector-container">
            <Hex value={hex} onhexeClick={() => handleClick(position, 7 * position + 6)} />
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

function PlayersInfo({ players }) {
    return (
        <div className="players-info">
            {players.map((player) => (
                <div key={player.id}>
                    <p>{player.user.username}: {player.score}</p>
                </div>
            ))}
        </div>
    );
}

function HostInfo({ host }) {
    return (
        <div className="host-info">
            <p>Puntuación: {host.score}</p>
            <p>Naves restantes: {host.numShips}</p>
        </div>
    );
}

export default function PlayGame() {
    const name = getIdFromUrl(3);
    //const [players, setPlayers] = useState(['X', 'O', 'Y']);
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

    const [gameInfo, setGameInfo] = useIntervalFetchState(
        [],
        `/api/v1/game/play/${name}`,
        jwt
    );

    const host = gameInfo.host;
    const players = gameInfo.players;
    const [winner, setWinner] = useState(null);

    function handleClick(position, i) {
        if (hexList[i]) {
            return;
        }
        let occuped = false;

        for (let j = 0; j < 7; j++) {
            if (hexList[position * 7 + j] !== null) return occuped = true;
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

    const MediaCard = ({ title, imageUrl, onUse, positionClass }) => {
        const mediaStyles = {
            height: '200px',
            backgroundImage: `url("${imageUrl}")`,
            backgroundSize: 'cover',
            backgroundPosition: 'center',
        };

        return (
            <div className={`cardStyles ${positionClass}`}>
                <div style={mediaStyles} title={title} />
                <div>
                    <h3 style={{ marginBottom: '8px', textAlign: 'center' }}>{title}</h3>
                </div>
                <div>
                    <button className="buttonStyles" onClick={onUse}>
                        Usar
                    </button>
                </div>
            </div>
        );
    };

    const handleExpand = () => {
        console.log('Usar expansión');
    }

    const handleExplore = () => {
        console.log('Usar explorar');
    }

    const handleExterminate = () => {
        console.log('Usar exterminar');
    }

    return (
        <div className="game">
            <PlayersInfo players={players}/>
            <HostInfo host={host}/>
            <div className="center-container">
                <div className="left-sector">
                    <Sector position={0} hexes={hexList.slice(0, 7)} handleClick={handleClick} />
                </div>
                <div className="right-sector">
                    <Sector position={1} hexes={hexList.slice(7, 14)} handleClick={handleClick} />
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
                    <Sector position={5} hexes={hexList.slice(28, 35)} handleClick={handleClick} />
                </div>
                <div className="right-sector">
                    <Sector position={6} hexes={hexList.slice(35, 42)} handleClick={handleClick} />
                </div>
            </div>
            <div className="cardsContainerStyle">
                <MediaCard title={"Expand"} imageUrl={expand} onUse={handleExpand} positionClass="left-card" />
                <MediaCard title={"Explore"} imageUrl={explore} onUse={handleExplore} positionClass="center-card" />
                <MediaCard title={"Exterminate"} imageUrl={exterminate} onUse={handleExterminate} positionClass="right-card" />
            </div>
        </div>

    );
}