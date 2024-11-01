import { useState } from "react";
import tokenService from "../services/token.service";
import { Link } from "react-router-dom";
import { Form, Input, Label } from "reactstrap";
import getErrorModal from "../util/getErrorModal";
import getIdFromUrl from "../util/getIdFromUrl";
import useFetchState from "../util/useFetchState";
import { SearchPlayer } from "../search/player/SearchPlayer";
import { PlayerResultsList } from "../search/player/PlayerResultsList";
import { SearchGame } from "../search/game/SearchGame";
import { GameResultsList } from "../search/game/GameResultsList";
import jwt_decode from "jwt-decode";


const jwt = tokenService.getLocalAccessToken();

export default function Createinvitation() {
    const id = getIdFromUrl(2);
    const emptyinvitation = {
        id: id === "new" ? null : id,
        isAccepted: false,
        discriminator: "FRIENDSHIP",
        playerTarget: null,
        playerSource: null,
        game: null
    };

    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [invitation, setInvitation] = useFetchState(
        emptyinvitation,
        `/api/v1/invitations`,
        jwt,
        setMessage,
        setVisible,
        id
    );

    const [playerResults, setplayerResults] = useState([]);
    const currentPlayerUsername = jwt ? jwt_decode(jwt).sub : null;
    const filteredPlayerResults = playerResults.filter(player => player.user.username !== currentPlayerUsername);


    const [gameResults, setGameResults] = useState([]);
    const [game, setGame] = useState(""); // Nuevo estado para el nombre del juego


    const modal = getErrorModal(setVisible, visible, message);

    function handleSubmit(event) {
        event.preventDefault();
        fetch(
            "/api/v1/invitations",
            {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: "application/json",
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(invitation),
            }
        )
            .then((response) => {
                if (!response.ok) {
                    return response.text().then((errorMessage) => {
                        alert(errorMessage);
                    });
                } else {
                    window.location.href = "/invitations";
                }
            })
    }



    function handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        if (name === 'playerTarget') {
            setInvitation({ ...invitation, playerTarget: value });
        }
        if (name === "discriminator") {
            setInvitation({ ...invitation, [name]: value });
            if (value === "FRIENDSHIP") {
                setGame("");
            }
        } else if (name === "game") {
            setGame(value);
        }

        else {
            setInvitation({ ...invitation, [name]: value });
        }
    }

    const handleSelectPlayer = (selectedPlayer) => {
        setplayerResults([]);
        setInvitation({ ...invitation, playerTarget: selectedPlayer });
    };

    const handleSelectGame = (selectedGame) => {
        setGameResults([]);
        setInvitation({ ...invitation, game: selectedGame });
    };

    return (
        <div className="auth-page-container">
            <h2 className="text-center">
                {"Create a New invitation"}
            </h2>
            <div className="auth-form-container">
                {modal}
                <Form onSubmit={handleSubmit}>
                    <div className="search-bar-container">
                        <SearchPlayer setResults={setplayerResults} onSelectPlayer={handleSelectPlayer} />
                        {playerResults.length === 0 && invitation.playerTarget && (
                            <div>{invitation.playerTarget.user.username}</div>
                        )}
                        {filteredPlayerResults.length > 0 && (
                            <PlayerResultsList results={filteredPlayerResults} onSelectPlayer={handleSelectPlayer} />
                        )}
                    </div>
                    <div className="custom-form-input">
                        <Label for="discriminator" className="custom-form-input-label">
                            Invitation type
                        </Label>
                        <Input
                            type="select"
                            required
                            name="discriminator"
                            id="discriminator"
                            value={invitation.discriminator || ""}
                            onChange={handleChange}
                            className="custom-input"
                        >
                            <option value="FRIENDSHIP">FRIENDSHIP</option>
                            <option value="GAME">GAME</option>
                        </Input>
                    </div>
                    {invitation.discriminator === "GAME" && (
                        <div className="search-bar-container">
                            <SearchGame setResults={setGameResults} onSelectGame={handleSelectGame} />
                            {gameResults.length === 0 && invitation.game && (
                                <div>{invitation.game.name}</div>
                            )}
                            {gameResults.length > 0 && (
                                <GameResultsList results={gameResults} onSelectGame={handleSelectGame} />
                            )}
                        </div>
                    )}
                    <div className="custom-button-row">
                        <button className="auth-button">Save</button>
                        <Link
                            to={`/invitations`}
                            className="auth-button"
                            style={{ textDecoration: "none" }}
                        >
                            Cancel
                        </Link>
                    </div>
                </Form>
            </div >
        </div >
    );
} 
