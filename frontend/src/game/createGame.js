import { useState } from "react";
import tokenService from "../services/token.service";
import { Link } from "react-router-dom";
import { Form, Input, Label } from "reactstrap";
import getErrorModal from "./../util/getErrorModal";
import getIdFromUrl from "./../util/getIdFromUrl";
import useFetchState from "./../util/useFetchState";

const jwt = tokenService.getLocalAccessToken();

export default function CreateGame() {
    const id = getIdFromUrl(2);
    const emptyGame = {
        id: id === "new" ? null : id,
        name: "",
        state: "LOBBY",
        publica: false,
    };
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [game, setGame] = useFetchState(
        emptyGame,
        `/api/v1/game/${id}`,
        jwt,
        setMessage,
        setVisible,
        id
    );
    const modal = getErrorModal(setVisible, visible, message);
    function handleSubmit(event) {
        event.preventDefault();
        fetch(
            "/api/v1/game" + (game.id ? "/" + game.id : ""),
            {
                method: game.id ? "PUT" : "POST",
                headers: {
                    Authorization: `Bearer ${jwt}`,
                    Accept: "application/json",
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(game),
            }
        )
            .then((response) => response.text())
            .then((data) => {
                if (data === "")
                    window.location.href = "/game";
                else {
                    let json = JSON.parse(data);
                    if (json.message) {
                        setMessage(JSON.parse(data).message);
                        setVisible(true);
                    } else
                        window.location.href = "/game";
                }
            })
            .catch((message) => alert(message));
    }

    function handleChange(event) {
        const { name, type, checked} = event.target;
        const newValue = type ==='checkbox' ? checked : event.target.value;
        setGame({...game, [name] : newValue});
        console.log("Game state:", game);
    }
    /*
    function handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        setGame({ ...game, [name]: value });
    }*/
    return (
        <div className="auth-page-container">
            <h2 className="text-center">
                {game.id ? "Edit Game" : "Create a New Game"}
            </h2>
            <div className="auth-form-container">
                {modal}
                <Form onSubmit={handleSubmit}>
                    <div className="custom-form-input">
                        <Label for="name" className="custom-form-input-label">
                            Game name
                        </Label>
                        <Input
                            type="text"
                            required
                            name="name"
                            id="name"
                            value={game.name || ""}
                            onChange={handleChange}
                            className="custom-input"
                        />
                    </div>
                    <div className="custom-form-input">
                        <Label for="publica" className="custom-form-input-label">
                            Partida pública
                        </Label>
                        <Input
                            type="checkbox"
                            
                            name="publica"
                            id="publica"
                            onChange={handleChange}
                            className="custom-input"
                            key="publicaCheckbox"
                            checked={game.publica}>
                        </Input>
                    </div>
                    <div className="custom-button-row">
                        <button className="auth-button">Save</button>
                        <Link
                            to={`/game`}
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
