import {
    Table, Button
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
    const player = tokenService.getUser();
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

    return (
        <div>
            <div className="admin-page-container">
                <h1 className="text-center" style={{ marginTop: "20px"}}>Tu Perfil</h1>
                <div>
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                            <tr>
                                <th className="text-center">Nombre</th>
                            </tr>
                        </thead>
                        <tbody>{player.username}</tbody>
                    </Table>

                </div>
                <h1 className="text-center" style={{ marginTop: "20px"}}>Tus Amigos</h1>
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
                    <Button outline color="success" >
                        <Link
                            to={'/invitations/new'} className="btn sm"
                            style={{ textDecoration: "none" }}>Add a friend</Link>
                    </Button>
                </div>
                <h1 className="text-center" style={{ marginTop: "20px"}}>Tus Partidas</h1>
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
        </div>
    );
}