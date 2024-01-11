import getIdFromUrl from "../../util/getIdFromUrl";
import useIntervalFetchState from "../../util/useIntervalFetchState";
import tokenService from "../../services/token.service";
import { useState } from "react";
import "./tablero.css"
import expand from "../../static/images/Expand.jpg"
import explore from "../../static/images/Explore.jpg"
import exterminate from "../../static/images/Exterminate.jpg"

const jwt = tokenService.getLocalAccessToken();


function Sector({ position, hexes, handleClick }) {
    let puntos = hexes.map((x) => x[0])
    return (
        <div className="sector-container">
            <div className="row-up">
                <Hex value={puntos[0]} onhexeClick={() => handleClick(position, 7 * position + 0)} />
                <Hex value={puntos[1]} onhexeClick={() => handleClick(position, 7 * position + 1)} />
            </div>
            <div>
                <Hex value={puntos[2]} onhexeClick={() => handleClick(position, 7 * position + 2)} />
                <Hex value={puntos[3]} onhexeClick={() => handleClick(position, 7 * position + 3)} />
                <Hex value={puntos[4]} onhexeClick={() => handleClick(position, 7 * position + 4)} />
            </div>
            <div className="row-down">
                <Hex value={puntos[5]} onhexeClick={() => handleClick(position, 7 * position + 5)} />
                <Hex value={puntos[6]} onhexeClick={() => handleClick(position, 7 * position + 6)} />
            </div>
        </div>
    )
}

function TriPrime({ position, hex, handleClick }) {
    return (
        <div className="sector-container">
            {hex && <Hex value={hex[0]} onhexeClick={() => handleClick(position, 7 * position + 6)} />}
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
    const [hexes, setHexes] = useState(
        [],
        `/api/v1/gameBoard/${name}`,
        jwt
    );

    const hexList =
        hexes.map((h) => {
            const newHex = [h.puntos, h.occuped, h.position]
            return (newHex)
        })

    const [gameInfo, setGameInfo] = useState(
        [],
        `/api/v1/game/play/${name}`,
        jwt
    );

    const host = gameInfo.host;
    const players = gameInfo.players;
    const [winner, setWinner] = useState(null);

    function handleClick(position, i) {
        fetch(
            "/api/v1/game/setHex/" + name + "/" +position + "/" +i, {
                method: "PUT",
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    Accept: "application/json",
                    "Content-Type": "application/json",
                },
            });
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
            {players && <PlayersInfo players={players}/>}
            {host && <HostInfo host={host}/>}
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