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
export default function GameList() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [alerts, setAlerts] = useState([]);
    const [player, setPlayer] = useFetchState(
        [],
        `/api/v1/player/details`,
        jwt
    );
    const [friends, setFriends] = useFetchState(
        [],
        `/api/v1/player/friends`,
        jwt
    );

    const playerDetails = 
        player.map((a) => {
            return (
            <tr key={a.id}>
                    <td className="text-center">{a.user.username}</td>
                    <td className="text-center">...</td>
            </tr>
            );
        });

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
                                    `/api/v1/game/${a.id}`,
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

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div>
            <div className="admin-page-container">
                <h1 className="text-center">Tu Perfil</h1>
                <div>
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                            <tr>
                                <th className="text-center">Nombre</th>
                                <th className="text-center">Estadísticas</th>
                            </tr>
                        </thead>
                        <tbody>{playerDetails}</tbody>
                    </Table>

                </div>
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
                    <Button outline color="success" >
                        <Link
                            to={'/invitations/new'} className="btn sm"
                            style={{ textDecoration: "none" }}>add a friend</Link>
                    </Button>
                </div>
            </div>
        </div>
    );
}