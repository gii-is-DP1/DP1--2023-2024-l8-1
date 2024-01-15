import {
    Table, Button
} from "reactstrap";

import tokenService from "../services/token.service";
import useFetchState from "../util/useFetchState";
import { Link } from "react-router-dom";
import deleteFromList from "../util/deleteFromList";
import { useState, useEffect } from "react";
import getErrorModal from "../util/getErrorModal";
import { useNavigate } from 'react-router-dom';


const jwt = tokenService.getLocalAccessToken();

export default function InvitationList() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [alerts, setAlerts] = useState([]);

    const [invitationsReceived, setInvitationsReceived] = useFetchState(
        [],
        `/api/v1/invitations/received`,
        jwt,
    );

    const [invitationsSent, setInvitationsSent] = useFetchState(
        [],
        `/api/v1/invitations/sent`,
        jwt,
    );


    const navigate = useNavigate();

    function aceptarInvitacion(a) {
        fetch(
            `/api/v1/invitations/accept/${a.id}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(a)
        });

        if (a.discriminator === "GAME") {

            fetch("/api/v1/game/join/" + a.game.name, {
                method: "PUT",
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    Accept: "application/json",
                    "Content-Type": "application/json",
                }
            });
            fetch(
                `/api/v1/invitations/${a.id}`, {
                method: "DELETE",
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    Accept: "application/json",
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(a)
            });
            alert("Invitación aceptada corrrectamente");
            navigate('../game/lobby/' + a.game.name);

        }

        if (a.discriminator === "FRIENDSHIP") {
            fetch("/api/v1/players/add/" + a.playerSource.id, {
                method: "PUT",
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    Accept: "application/json",
                    "Content-Type": "application/json",
                }
            });
            fetch(
                `/api/v1/invitations/${a.id}`, {
                method: "DELETE",
                headers: {
                    "Authorization": `Bearer ${jwt}`,
                    Accept: "application/json",
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(a)
            })
                .then(() => alert("Invitación aceptada correctamente"))
                .catch(error => alert(error))
        }
    }

    const invitationList =
        invitationsReceived.map((a) => {
            return (
                <tr key={a.id}>
                    <td className="text-center">{a.discriminator}</td>
                    <td className="text-center">{a.playerSource.user.username}</td>
                    <td className="text-center">
                        <Button outline color="success"
                            onClick={() => aceptarInvitacion(a)
                            }>
                            Aceptar
                        </Button>
                    </td>
                    <td className="text-center">
                        <Button outline color="danger"
                            onClick={() =>
                                deleteFromList(
                                    `/api/v1/invitations/${a.id}`,
                                    a.id,
                                    [invitationsReceived, setInvitationsReceived],
                                    [alerts, setAlerts],
                                    setMessage,
                                    setVisible
                                )}>
                            Rechazar
                        </Button>
                    </td>
                </tr>
            );
        });

    const invitationSent =
        invitationsSent.map((a) => {
            return (
                <tr key={a.id}>
                    <td className="text-center">{a.discriminator}</td>
                    <td className="text-center">{a.playerTarget.user.username}</td>
                    <td className="text-center">{a.isAccepted === false ? "Pendiente" : "Aceptada"}</td>
                </tr>
            );
        });




    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div>
            <div className="admin-page-container">
                <h1 className="text-center">Invitations received</h1>
                <div>
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                            <tr>
                                <th className="text-center">Invitation Type</th>
                                <th className="text-center">Player</th>
                                <th className="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>{invitationList}</tbody>
                    </Table>

                </div>
                <h1 className="text-center">Invitations sent</h1>
                <div>
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                            <tr>
                                <th className="text-center">Invitation Type</th>
                                <th className="text-center">Player</th>
                                <th className="text-center">Accepted</th>
                            </tr>
                        </thead>
                        <tbody>{invitationSent}</tbody>
                    </Table>
                    <Button outline color="success" >
                        <Link
                            to={'/invitations/new'} className="btn sm"
                            style={{ textDecoration: "none" }}>Create new invitation</Link>
                    </Button>
                </div>
            </div>
        </div>
    );
}