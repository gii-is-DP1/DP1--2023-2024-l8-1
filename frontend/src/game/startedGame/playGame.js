import getIdFromUrl from "../../util/getIdFromUrl";
import useIntervalFetchState from "../../util/useIntervalFetchState";
import useFetchState from "../../util/useFetchState";
import tokenService from "../../services/token.service";
import { useState } from "react";
import "./tablero.css"
import expand from "../../static/images/Expand.jpg"
import explore from "../../static/images/Explore.jpg"
import exterminate from "../../static/images/Exterminate.jpg"
import stars from "../../static/images/Stars.jpg"
import planet1 from "../../static/images/Planeta1.jpg"
import planet2 from "../../static/images/Planeta2.jpg"
import triPrime from "../../static/images/PlanetaTriPrime.jpg"
import { useNavigate } from 'react-router-dom';

const jwt = tokenService.getLocalAccessToken();


function Sector({ host, players, position, hexes, ships, handleClick, style }) {
    let puntos = hexes.map((x) => x[0]);
    let positions = hexes.map((x) => x[2]);

    const sectorStyles = {
        transform: 'rotate(-30deg)',
        ...style
    };


    return (
        <div className="sector-container" style={sectorStyles}>
            <div className="row-up">
                <Hex host={host} players={players} value={puntos[0]} hexPosition={positions[0]} ships={ships} onHexClick={() => handleClick(position, 7 * position + 0)} />
                <Hex host={host} players={players} value={puntos[1]} hexPosition={positions[1]} ships={ships} onHexClick={() => handleClick(position, 7 * position + 1)} />
            </div>
            <div>

                <Hex host={host} players={players} value={puntos[2]} hexPosition={positions[2]} ships={ships} onHexClick={() => handleClick(position, 7 * position + 2)} />
                <Hex host={host} players={players} value={puntos[3]} hexPosition={positions[3]} ships={ships} onHexClick={() => handleClick(position, 7 * position + 3)} />
                <Hex host={host} players={players} value={puntos[4]} hexPosition={positions[4]} ships={ships} onHexClick={() => handleClick(position, 7 * position + 4)} />
            </div>
            <div className="row-down">
                <Hex host={host} players={players} value={puntos[5]} hexPosition={positions[5]} ships={ships} onHexClick={() => handleClick(position, 7 * position + 5)} />
                <Hex host={host} players={players} value={puntos[6]} hexPosition={positions[6]} ships={ships} onHexClick={() => handleClick(position, 7 * position + 6)} />


            </div>
        </div>
    );
}

function TriPrime({ host, players, position, hex, ships, handleClick, style }) {

    const sectorStyles = {
        transform: 'rotate(-30deg)',
        ...style
    };
    return (
        <div className="sector-container" style={sectorStyles}>
            <div className="row-up">
                {hex && <Hex host={host} players={players} value={hex[0]} ships={ships} onHexClick={() => handleClick(position, 7 * position + 0)} />}
                {hex && <Hex host={host} players={players} value={hex[0]} ships={ships} onHexClick={() => handleClick(position, 7 * position + 0)} />}
            </div>
            <div>

                {hex && <Hex host={host} players={players} value={hex[0]} ships={ships} onHexClick={() => handleClick(position, 7 * position + 0)} />}

                {hex && <Hex host={host} players={players} value={hex[0]} hexPosition={7 * position + 0} ships={ships} onHexClick={() => handleClick(position, 7 * position + 0)} />}

                {hex && <Hex host={host} players={players} value={hex[0]} ships={ships} onHexClick={() => handleClick(position, 7 * position + 0)} />}
            </div>
            <div className="row-down">

                {hex && <Hex host={host} players={players} value={hex[0]} ships={ships} onHexClick={() => handleClick(position, 7 * position + 0)} />}

                {hex && <Hex host={host} players={players} value={hex[0]} ships={ships} onHexClick={() => handleClick(position, 7 * position + 0)} />}
            </div>
        </div>
    );
}


function Hex({ host, players, value, hexPosition, ships, onHexClick }) {

    let image;

    switch (value) {
        case 0:
            image = stars;
            break;
        case 1:
            image = planet1;
            break;
        case 2:
            image = planet2;
            break;
        case 3:
            image = triPrime;
            break;
        default:
            image = stars;
    }

    const hexStyles = {
        backgroundImage: `url("${image}")`,
        backgroundSize: 'cover',
    }

    const renderShips = () => {
        // Filtra las naves para este hexágono
        const shipsInHex = ships.filter((ship) => ship.hex !== null && ship.hex.position === hexPosition);
        if (shipsInHex.length !== 0) {
            let numeroColor = 0;
            let owner = shipsInHex[0].player.user.username;
            if (owner === host.user.username) {
                numeroColor = 1;
            } else if (owner === players[0].user.username) {
                numeroColor = 2;
            } else if (owner === players[1].user.username) {
                numeroColor = 3;
            }
            return (
                <div className="ships-container">
                    <div className={`ship player-${numeroColor}`}>
                        <div className="ship-count">{shipsInHex.length}</div>
                    </div>
                </div>
            );
        }
    };

    return (
        <button className="hex" style={hexStyles} onClick={onHexClick}>
            {ships && renderShips()}
        </button>
    );
}


function PlayersInfo({ player, playerShips, host, players }) {
    const loggedUser = tokenService.getUser()
    let numeroColor = 0;
    if (player === host) {
        numeroColor = 1;
    } else if (player === players[0]) {
        numeroColor = 2;
    } else if (player === players[1]) {
        numeroColor = 3;
    }
    return (
        <div className="players-info">
            <p style={{ color: player.user.username === loggedUser.username ? 'red' : 'black' }}>{player.user.username}</p>
            <p>Naves restantes: {playerShips}</p>
            <p>Puntuación: {player.score}</p>
            <p></p>
        </div>
    );
}

export default function PlayGame() {
    const name = getIdFromUrl(3);

    const navigate = useNavigate();

    const [gameInfo, setGameInfo] = useFetchState(
        [],
        `/api/v1/game/play/${name}`,
        jwt
    );

    if (gameInfo.state === "OVER") {
        navigate('../game/over/' + name);
    }

    const [hexes, setHexes] = useFetchState(
        [],
        `/api/v1/gameBoard/${name}`,
        jwt
    );

    const [shipList, setShips] = useIntervalFetchState(
        [],
        `/api/v1/game/play/${name}/ships`,
        jwt
    );

    const [isInitial, setIsInitial] = useIntervalFetchState(
        [],
        `/api/v1/game/isInitial/${name}`,
        jwt
    );

    const [currentTurn, setCurrentTurn] = useIntervalFetchState(
        [],
        `/api/v1/game/getCurrentTurn/${name}`,
        jwt
    );

    const [currentPhase, setCurrentPhase] = useIntervalFetchState(
        [],
        `/api/v1/game/getCurrentPhase/${name}`,
        jwt
    );

    const [currentAction, setCurrentAction] = useIntervalFetchState(
        [],
        `/api/v1/game/getAction/${name}`,
        jwt
    );

    const generateSectorStyles = (position) => {
        // Genera estilos específicos para cada Sector
        switch (position) {
            case 0:
                return { marginTop: '10px', marginLeft: '-35px', marginBottom: '-70px' };
            case 1:
                return { marginTop: '-40px', marginLeft: '-10px', marginBottom: '-70px' };
            case 2:
                return { marginTop: '10px', marginLeft: '30px' };
            case 3:
                return { marginTop: '-40px', marginLeft: '-30px' };
            case 4:
                return { marginTop: '-90px', marginLeft: '-30px' };
            case 5:
                return { marginTop: '-80px', marginLeft: '90px' };
            case 6:
                return { marginTop: '-140px', marginLeft: '-10px' };
            default:
                return {};
        }
    };

    const hexList =
        hexes.map((h) => {
            const newHex = [h.puntos, h.occuped, h.position]
            return (newHex)
        })

    const host = gameInfo.host;
    const players = gameInfo.players;
    let primerJugador;
    let segundoJugador;
    let hostUsername;
    // const hostUsername = host.user.username;
    if (players && players.length >= 2) {
        primerJugador = players[0].user.username;
        segundoJugador = players[1].user.username;
        hostUsername = host.user.username;
    } else {
        console.error("La matriz 'players' no tiene al menos dos elementos.");
    }
    // const primerJugadorUsername = primerJugador.user.username;
    // const segundoJugadorUsername = segundoJugador.user.username;
    // const hostUsername = host.user.username;

    const [hostShips, setHostShips] = useIntervalFetchState(
        [],
        `/api/v1/players/${hostUsername}/remainingShips`,
        jwt
    )
    const [player1Ships, setPlayer1Ships] = useIntervalFetchState(
        [],
        `/api/v1/players/${primerJugador}/remainingShips`,
        jwt
    )
    const [player2Ships, setPlayer2Ships] = useIntervalFetchState(
        [],
        `/api/v1/players/${segundoJugador}/remainingShips`,
        jwt
    )

    const [winner, setWinner] = useState(null);
    const [selectedFunction, setSelectedFunction] = useState(null);
    const [selectedOriginHex, setSelectedOriginHex] = useState(null);
    const [selectedTargetHex, setSelectedTargetHex] = useState(null);

    function handleClick(sector, position) {
        fetch(
            "/api/v1/game/setHex/" + name + "/" + sector + "/" + position, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
        }).then((response) => {
            if (!response.ok) {
                return response.json().then((errorData) => {
                    let errorMessage = errorData.message;
                    alert(errorMessage);
                });
            }
        })
    }

    const handleHexClick = (sector, position) => {
        if (isInitial || currentPhase.isPoint) {
            handleClick(sector, position)
        } else if (!isInitial && !currentPhase.isPoint) {
            if (currentAction[0] === "EXPAND") {
                handleExpand(position)
            } else {
                if (selectedOriginHex === null) {
                    setSelectedOriginHex(position)
                } else if (selectedTargetHex === null) {
                    setSelectedTargetHex(position)
                    handleAction(currentAction[0], selectedOriginHex, position)
                    setSelectedFunction(null)
                    setSelectedOriginHex(null)
                    setSelectedTargetHex(null)
                }
            }
        }
    }

    const handleAction = (selectedFunction, hexPositionOrigin, hexPositionTarget) => {
        if (selectedFunction === "EXPLORE") {
            handleExplore(hexPositionOrigin, hexPositionTarget)
        } else if (selectedFunction === "EXTERMINATE") {
            handleExterminate(hexPositionOrigin, hexPositionTarget)
        }
    }

    const handleFunctionSelection = (selectedFunction) => {
        setSelectedFunction(selectedFunction);
    }

    const handleExpand = (hexPosition) => {
        fetch(`/api/v1/game/play/${name}/expand/${hexPosition}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${jwt}`,
            },
        }).then((response) => {
            if (!response.ok) {
                const contentType = response.headers.get("Content-Type");
                if (contentType && contentType.includes("application/json")) {
                    return response.json().then((errorData) => {
                        let errorMessage = errorData.message;
                        alert(errorMessage);
                    });
                } else {
                    return response.text().then((errorMessage) => {
                        alert(errorMessage);
                    });
                }
            }
        })
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
                const contentType = response.headers.get("Content-Type");
                if (contentType && contentType.includes("application/json")) {
                    return response.json().then((errorData) => {
                        let errorMessage = errorData.message;
                        alert(errorMessage);
                    });
                } else {
                    return response.text().then((errorMessage) => {
                        alert(errorMessage);
                    });
                }
            }
        })
        console.log("Has usado Explorar")
    }

    const handleExterminate = (hexPositionOrigin, hexPositionTarget) => {
        fetch(`/api/v1/game/play/${name}/exterminate/${hexPositionOrigin}/${hexPositionTarget}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${jwt}`,
            },
        }).then((response) => {
            if (!response.ok) {
                const contentType = response.headers.get("Content-Type");
                if (contentType && contentType.includes("application/json")) {
                    return response.json().then((errorData) => {
                        let errorMessage = errorData.message;
                        alert(errorMessage);
                    });
                } else {
                    return response.text().then((errorMessage) => {
                        alert(errorMessage);
                    });
                }
            }
        })
        console.log("Has usado Exterminate")
    }

    const [expandOrder, setExpandOrder] = useState(null); // Estado para el valor seleccionado
    const [exploreOrder, setExploreOrder] = useState(null);
    const [exterminateOrder, setExterminateOrder] = useState(null);
    const [playerCards, setPlayerCards] = useIntervalFetchState(
        [],
        `/api/v1/cards`,
        jwt
    );

    const playerCardsList =
        playerCards.map((c) => {
            const card = c.performingOrder
            return (card)
        })

    const handleChangeOrder = (cardType, order) => {
        // Lógica para manejar el cambio de orden y realizar la llamada a la API
        if (cardType) {
            // Utilizar el tipo de carta correspondiente
            switch (cardType) {
                case 'expand':
                    setExpandOrder(order);
                    console.log(cardType, order)
                    console.log(playerCardsList[0][0])
                    break;
                case 'explore':
                    setExploreOrder(order);
                    console.log(cardType, order)
                    console.log(playerCardsList[1])
                    break;
                case 'exterminate':
                    setExterminateOrder(order);
                    console.log(cardType, order)
                    console.log(playerCardsList[2])
                    break;
                default:
                    break;
            }

            fetch(`/api/v1/cards/${name}/${cardType}/${order}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${jwt}`,
                },
            }).then((response) => {
                if (!response.ok) {
                    return response.json().then((errorData) => {
                        let errorMessage = errorData.message;
                        alert(errorMessage);
                    });
                }
            })
        }

    }


    const MediaCard = ({ title, imageUrl, onUse, positionClass }) => {

        const mediaStyles = {
            height: '200px',
            width: '150px',
            backgroundImage: `url("${imageUrl}")`,
            backgroundSize: 'cover',
            backgroundPosition: 'center',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '10px',
            borderRadius: '10px',
        };

        const textStyles = {
            fontSize: '14px',
            marginBottom: '8px',
            textAlign: 'center',
        };

        const buttonStyles = {
            fontSize: '14px',
            width: '100%',
            marginBottom: "8px"
        };

        return (
            <div className={`cardStyles ${positionClass}`}>
                <div style={mediaStyles} title={title} />
                <div>
                    <h3 style={textStyles}>{title}</h3>
                </div>
            </div>
        );
    };

    const handleSkip = () => {
        fetch(`/api/v1/game/skipTurn/${name}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
        }).then((response) => {
            if (!response.ok) {
                return response.json().then((errorData) => {
                    let errorMessage = errorData.message;
                    alert(errorMessage);
                });
            }
        })
    }

    const handleSetOrder = () => {
        fetch(`/api/v1/game/setOrder/${name}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
        }).then((response) => {
            if (!response.ok) {
                return response.json().then((errorData) => {
                    let errorMessage = errorData.message;
                    alert(errorMessage);
                });
            }
        })
    }

    return (
        <div className="game">
            <div className="players-info-container">
                {host && host.user && <PlayersInfo player={host} playerShips={hostShips} host={host} players={players} />}
                {players && <PlayersInfo player={players[0]} playerShips={player1Ships} host={host} players={players} />}
                {players && <PlayersInfo player={players[1]} playerShips={player2Ships} host={host} players={players} />}
            </div>
            <div>
                <p>Es turno de:</p>
                <p>{currentTurn[0]}</p>
                {isInitial && !currentPhase.isOrder && <p>Elige un Hexágono</p>}{currentPhase.isOrder && <p>Ordena tus cartas</p>}{currentPhase.isPoint && <p>Elige un sector para puntuar</p>}{currentAction[0] !== "nada" && <p>{currentAction}</p>}

            </div>
            <div className="center-container">
                <div className="left-sector">
                    <Sector
                        host={host}
                        players={players}
                        position={0}
                        hexes={hexList.slice(0, 7)}
                        ships={shipList}
                        handleClick={handleHexClick}
                        style={generateSectorStyles(0)}
                    />
                </div>
                <div className="right-sector">
                    <Sector
                        host={host}
                        players={players}
                        position={1}
                        hexes={hexList.slice(7, 14)}
                        ships={shipList}
                        handleClick={handleHexClick}
                        style={generateSectorStyles(1)}
                    />
                </div>
            </div>
            <div className="center-container">
                <div className="left-sector">
                    <Sector
                        host={host}
                        players={players}
                        position={2}
                        hexes={hexList.slice(14, 21)}
                        ships={shipList}
                        handleClick={handleHexClick}
                        style={generateSectorStyles(2)}
                    />
                </div>
                <div className="tri-prime-container">
                    <TriPrime
                        host={host}
                        players={players}
                        position={6}
                        hex={hexList[42]}
                        ships={shipList}
                        handleClick={handleHexClick}
                        style={generateSectorStyles(3)}
                    />
                </div>
                <div className="right-sector">
                    <Sector
                        host={host}
                        players={players}
                        position={3}
                        hexes={hexList.slice(21, 28)}
                        ships={shipList}
                        handleClick={handleHexClick}
                        style={generateSectorStyles(4)}
                    />
                </div>
            </div>
            <div className="center-container">
                <div className="left-sector">
                    <Sector
                        host={host}
                        players={players}
                        position={4}
                        hexes={hexList.slice(28, 35)}
                        ships={shipList}
                        handleClick={handleHexClick}
                        style={generateSectorStyles(5)}
                    />
                </div>
                <div className="right-sector">
                    <Sector
                        host={host}
                        players={players}
                        position={5}
                        hexes={hexList.slice(35, 41)}
                        ships={shipList}
                        handleClick={handleHexClick}
                        style={generateSectorStyles(6)}
                    />
                </div>
            </div>
            <div className="cardsContainerStyle">
                <div className="cardContainer">
                    <MediaCard title={"Expand"} imageUrl={expand} positionClass="left-card" />
                    {currentPhase.isOrder && <select
                        value={playerCardsList[0] + 1}
                        onChange={(e) => handleChangeOrder("expand", parseInt(e.target.value))}
                    >
                        <option value={1}>1</option>
                        <option value={2}>2</option>
                        <option value={3}>3</option>
                    </select>}
                </div>
                <div className="cardContainer">
                    <MediaCard title={"Explore"} imageUrl={explore} positionClass="center-card" />
                    {currentPhase.isOrder && <select
                        value={playerCardsList[1] + 1}
                        onChange={(e) => handleChangeOrder("explore", parseInt(e.target.value))}
                    >
                        <option value={1}>1</option>
                        <option value={2}>2</option>
                        <option value={3}>3</option>
                    </select>}
                </div>

                <div className="cardContainer">
                    <MediaCard title={"Exterminate"} imageUrl={exterminate} positionClass="right-card" />
                    {currentPhase.isOrder && <select
                        value={playerCardsList[2] + 1}
                        onChange={(e) => handleChangeOrder("exterminate", parseInt(e.target.value))}
                    >
                        <option value={1}>1</option>
                        <option value={2}>2</option>
                        <option value={3}>3</option>
                    </select>}
                </div>
                <div>
                    {!isInitial && !currentPhase.isOrder && !currentPhase.isPoint && <button onClick={() => handleSkip()}>Pasar</button>}
                </div>
                <div>
                    {currentPhase.isOrder && <button onClick={() => handleSetOrder()}>Ordenar Cartas</button>}
                </div>

            </div>
        </div>
    );
}