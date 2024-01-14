import {
    Table, Button, Input
} from "reactstrap";

import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { Link } from "react-router-dom";
import deleteFromList from "../util/deleteFromList";
import { useState } from "react";
import getErrorModal from "../util/getErrorModal";

const jwt = tokenService.getLocalAccessToken();

export default function PlayerDetails() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [alerts, setAlerts] = useState([]);
    const [loggedPlayer, setLoggedPlayer] = useFetchState(
        [],
        `/api/v1/players/details`,
        jwt
    )
    const [friends, setFriends] = useFetchState(
        [],
        `/api/v1/players/friends`,
        jwt
    );

    const [playerGames, setPlayerGames] = useFetchState(
        [],
        `/api/v1/game/currentPlayerGames`,
        jwt
    )

    const friendsList =
        friends.map((a) => {
            return (
                <tr key={a.id}>
                    <td className="text-center">{a.user.username}</td>
                    <td className="text-center">{a.rol === 0 || 2 ? "Jugando" : "En Espera"}</td>
                    <td className="text-center"> ... </td>
                    <td className="text-center">
                        <Button outline color="danger"
                            onClick={() =>
                                deleteFromList(
                                    `/api/v1/players/delete/${a.id}`,
                                    a.id,
                                    [friends, setFriends],
                                    [alerts, setAlerts],
                                    setMessage,
                                    setVisible
                                )}>
                            Delete
                        </Button>
                    </td>
                </tr>
            );
        });

    const playerGamesList =
        playerGames.map((g) => {
            return (
                <tr key={g.id}>
                    <td className="text-center">{g.name}</td>
                    <td className="text-center">{g.host.user.username}</td>
                    <td className="text-center">{g.state}</td>
                    <td className="text-center">{g.winner && g.winner.user.username}</td>
                </tr>
            );
        });

    const modal = getErrorModal(setVisible, visible, message);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setLoggedPlayer({
            ...loggedPlayer,
            [name]: value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch(`/api/v1/players/${loggedPlayer.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${jwt}`,
                },
                body: JSON.stringify(loggedPlayer),
            });

            if (response.ok) {
                alert('Perfil actualizado con éxito');
                setVisible(true);
            } else {
                alert('Error al actualizar el perfil');
                setVisible(true);
            }
        } catch (error) {
            console.error('Error:', error);
            setMessage('Error de conexión');
            setVisible(true);
        }
    };


    return (
        <div>
            <div className="admin-page-container">
                <h1 className="text-center">Tu Perfil</h1>
                {loggedPlayer && Object.keys(loggedPlayer).length > 0 && (
                    <form>
                        <div>
                            <label>First Name:</label>
                            <Input
                                type="text"
                                name="firstName"
                                value={loggedPlayer.firstName}
                                onChange={handleInputChange}
                            />
                        </div>
                        <div>
                            <label>Last Name:</label>
                            <Input
                                type="text"
                                name="lastName"
                                value={loggedPlayer.lastName}
                                onChange={handleInputChange}
                            />
                        </div>
                        <Button style={{ display: 'block', margin: 'auto' }} outline color="success" type="submit" onClick={handleSubmit}>
                            Guardar cambios
                        </Button>
                    </form>
                )}

                <h1 className="text-center">Tus Amigos</h1>
                <div>
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                            <tr>
                                <th className="text-center">Nombre</th>
                                <th className="text-center">Estado</th>
                                <th className="text-center">Estadísticas</th>
                                <th className="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>{friendsList}</tbody>
                    </Table>
                    <Button outline color="success" style={{ display: 'block', margin: 'auto' }} >
                        <Link
                            to={'/invitations/new'} className="btn sm"
                            style={{ textDecoration: "none" }}>Add a friend</Link>
                    </Button>
                </div>
            </div>
            <h1 className="text-center" style={{ marginTop: "20px" }}>Tus Partidas</h1>
            <div>
                <Table aria-label="achievements" className="mt-4">
                    <thead>
                        <tr>
                            <th className="text-center">Partida</th>
                            <th className="text-center">Host</th>
                            <th className="text-center">Estado</th>
                            <th className="text-center">Ganador</th>
                        </tr>
                    </thead>
                    <tbody>{playerGamesList}</tbody>
                </Table>

            </div>
        </div>
    );
}