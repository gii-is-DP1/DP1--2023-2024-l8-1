import getIdFromUrl from "../../util/getIdFromUrl";
import useIntervalFetchState from "../../util/useIntervalFetchState";
import useFetchState from "../../util/useFetchState";
import tokenService from "../../services/token.service";
import { useState } from "react";
import "./tablero.css"
import expand from "../../static/images/Expand.jpg"
import explore from "../../static/images/Explore.jpg"
import exterminate from "../../static/images/Exterminate.jpg"

const jwt = tokenService.getLocalAccessToken();


function Sector({ position, hexes,  onClickSetUpShips, onHexClick }) {
    let puntos = hexes.map((x) => x[1])
    let hexIds = hexes.map((x) => x[0])
    let positions = hexes.map((x) => x[3])
    return (
        <div className="sector-container">
            <div className="row-up">
                <Hex value={puntos[0]} position={positions[0]} onHexClick={() => onHexClick(positions[0])} />
                <Hex value={puntos[1]} position={positions[1]} onHexClick={() => onHexClick(positions[1])} />
            </div>
            <div>
                <Hex value={puntos[2]} position={positions[2]} onHexClick={() => onHexClick(positions[2])} />
                <Hex value={puntos[3]} position={positions[3]} onHexClick={() => onHexClick(positions[3])} />
                <Hex value={puntos[4]} position={positions[4]} onHexClick={() => onHexClick(positions[4])} />
            </div>
            <div className="row-down">
                <Hex value={puntos[5]} position={positions[5]} onHexClick={() => onHexClick(positions[5])} />
                <Hex value={puntos[6]} position={positions[6]} onHexClick={() => onHexClick(positions[6])} />
            </div>
        </div>
    )
}

function TriPrime({ position, hex, handleClick }) {
    return (
        <div className="sector-container">
            {hex && <Hex value={hex[1]} onhexeClick={() => handleClick(position, 7 * position + 6)} />}
        </div>
    );
}


function Hex({ value, position, onHexClick }) {
    return (
        <button className="hex" onClick={() => onHexClick(position)}>
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

    const [gameInfo, setGameInfo] = useFetchState(
        [],
        `/api/v1/game/play/${name}`,
        jwt
    );

    const host = gameInfo.host;
    const players = gameInfo.players;
    const [winner, setWinner] = useState(null);
    const [selectedFunction, setSelectedFunction] = useState(null);
    const [selectedOriginHex, setSelectedOriginHex] = useState(null);
    const [selectedTargetHex, setSelectedTargetHex] = useState(null);

    const handleFunctionSelection = (selectedFunction) => {
        setSelectedFunction(selectedFunction);
    }

    const handleHexClick = (position) => {
        if (selectedOriginHex === null) {
            setSelectedOriginHex(position)
        } else if (selectedTargetHex === null) {
            setSelectedTargetHex(position)
            handleAction(selectedFunction, selectedOriginHex, position)
            setSelectedFunction(null)
            setSelectedOriginHex(null)
            setSelectedTargetHex(null)
        }
    }

    const handleAction = (selectedFunction, hexPositionOrigin, hexPositionTarget) => {
        if (selectedFunction === "expand") {
            handleExpand(hexPositionTarget)
        } else if (selectedFunction === "explore") {
            handleExplore(hexPositionOrigin, hexPositionTarget)
        } else if (selectedFunction === "exterminate") {
            handleExterminate(hexPositionOrigin, hexPositionTarget)
        }
        

    }

    function onClickSetUpShips(hexId) {
    
            fetch(`/api/v1/game/play/${name}/${hexId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}`
                },
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                })
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

    const handleExpand = (hexPosition) => {
        fetch(`/api/v1/game/play/${name}/expand/${hexPosition}`, {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${jwt}`,
          },
        }).then((response) => {
          if (!response.ok) {
            throw new Error("Network response was not ok");
          }
        });
        console.log("Has usado Expandir")
      };

    const handleExplore = (hexPositionOrigin, hexPositionTarget) => {
        fetch(`/api/v1/game/play/${name}/explore/${hexPositionOrigin}/${hexPositionTarget}`, {
            method: "PUT",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${jwt}`,
            },
          }).then((response) => {
            if (!response.ok) {
              throw new Error("Network response was not ok");
            }
          });
          console.log("Has usado Explorar")
    }

    const handleExterminate = () => {
        console.log('Usar exterminar');
    }

    return (
        <div className="game">
            {players && <PlayersInfo players={players} />}
            {host && <HostInfo host={host} />}
            <div className="center-container">
                <div className="left-sector">
                    <Sector position={0} hexes={hexList.slice(0, 7)} onClickSetUpShips={onClickSetUpShips} onHexClick={handleHexClick}/>
                </div>
                <div className="right-sector">
                    <Sector position={1} hexes={hexList.slice(7, 14)} onClickSetUpShips={onClickSetUpShips} onHexClick={handleHexClick}/>
                </div>
            </div>
            <div className="center-container">
                <div className="left-sector">
                    <Sector position={2} hexes={hexList.slice(14, 21)} onClickSetUpShips={onClickSetUpShips} onHexClick={handleHexClick}/>
                </div>
                <div className="tri-prime-container">
                    <TriPrime position={3} hex={hexList[42]} onClickSetUpShips={onClickSetUpShips}/>
                </div>
                <div className="right-sector">
                    <Sector position={4} hexes={hexList.slice(21, 28)} onClickSetUpShips={onClickSetUpShips} onHexClick={handleHexClick}/>
                </div>
            </div>
            <div className="center-container">
                <div className="left-sector">
                    <Sector position={5} hexes={hexList.slice(28, 35)} onClickSetUpShips={onClickSetUpShips} onHexClick={handleHexClick}/>
                </div>
                <div className="right-sector">
                    <Sector position={6} hexes={hexList.slice(35, 42)} onClickSetUpShips={onClickSetUpShips} onHexClick={handleHexClick}/>
                </div>
            </div>
            <div className="cardsContainerStyle">
                <MediaCard title={"Expand"} imageUrl={expand} onUse={() => handleFunctionSelection("expand")} positionClass="left-card" />
                <MediaCard title={"Explore"} imageUrl={explore} onUse={() => handleFunctionSelection("explore")} positionClass="center-card" />
                <MediaCard title={"Exterminate"} imageUrl={exterminate} onUse={handleExterminate} positionClass="right-card" />
            </div>
        </div>

    );
}