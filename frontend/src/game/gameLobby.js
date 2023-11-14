import {
    Table, Button
} from "reactstrap";

import getIdFromUrl from "./../util/getIdFromUrl";
import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { Link } from "react-router-dom";
import deleteFromList from "../util/deleteFromList";
import { useState } from "react";
import getErrorModal from "../util/getErrorModal";

const jwt = tokenService.getLocalAccessToken();
export default function GameLobby() {
    const name = getIdFromUrl(3);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [alerts, setAlerts] = useState([]);
    const [players, setPlayers] = useFetchState(
        [],
        `/api/v1/game/lobby/${name}`,
        jwt
    );
    const modal = getErrorModal(setVisible, visible, message);
    const playersList =
        players.map((a) => {
            return (
                <tr key={a.id}>
                    <td className="text-center">{a.rol}</td>
                    <td className="text-center">{a.user.username}</td>
                    <td className="text-center">
                        <Button outline color="danger"
                            onClick={() =>
                                deleteFromList(
                                    `/api/v1/game/lobby/${name}/${a.id}`,
                                    a.id,
                                    [players, setPlayers],
                                    [alerts, setAlerts],
                                    setMessage,
                                    setVisible,
                                    modal
                                )}>
                            Kick Player
                        </Button>
                    </td>
                </tr>
            );
        });

    return (
        <div>
            <div className="admin-page-container">
                <h1 className="text-center">Game's Players</h1>
                <div>
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                            <tr>
                                <th className="text-center">Rol</th>
                                <th className="text-center">Name</th>
                                <th className="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>{playersList}</tbody>
                    </Table>
                    <Button outline color="success" >
                        <Link
                            to={'/game/new'} className="btn sm"
                            style={{ textDecoration: "none" }}>Start Game</Link>
                    </Button>
                </div>
            </div>
        </div>
    );
}