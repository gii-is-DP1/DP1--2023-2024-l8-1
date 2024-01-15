import {
    Table, Button
} from "reactstrap";

import getIdFromUrl from "../../util/getIdFromUrl";
import tokenService from "../../services/token.service";
import useIntervalFetchState from "../../util/useIntervalFetchState";
import useFetchState from "../../util/useFetchState";
import deleteFromList from "../../util/deleteFromList";
import { Link } from "react-router-dom";
import { useState } from "react";
import getErrorModal from "../../util/getErrorModal";
import { useNavigate } from 'react-router-dom';

const jwt = tokenService.getLocalAccessToken();
export default function GameLobby() {
    const name = getIdFromUrl(3);
    const loggedUser = tokenService.getUser()
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [alerts, setAlerts] = useState([]);
    const navigate = useNavigate();
    const [players, setPlayers] = useFetchState(
        [],
        `/api/v1/game/lobby/${name}`,
        jwt
    );

    const [gameInfo, setGameInfo] = useIntervalFetchState(
        [],
        `/api/v1/game/play/${name}`,
        jwt
    );

    if (gameInfo.state != null) {
        if (gameInfo.state !== "LOBBY" && gameInfo.state !== "OVER") {
            navigate('../game/play/' + name);
        }
    }



    function startGame(name) {
        fetch(
            "/api/v1/game/start/" + name, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
        });


        fetch(
            "/api/v1/gameBoard/" + name, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
        });

        navigate('../game/play/' + name);

    }

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
                <h1 className="text-center" style={{ marginTop: "20px" }}>Game's Players</h1>
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

                    {gameInfo.host && loggedUser.username === gameInfo.host.user.username && <Button outline color="success"
                        onClick={() => startGame(name)}>start
                    </Button>}
                    <Button outline color="warning" onClick={() => navigate('../invitations/new/')}>
                        Invite a friend
                    </Button>
                </div>
            </div>
        </div>
    );
}