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

export default function InvitationList() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [alerts, setAlerts] = useState([]);
    const [invitationsReceived, setInvitationsReceived] = useFetchState(
        [],
        `/api/v1/invitations/received`,
        jwt
    );

    const [invitationsSent, setInvitationsSent] = useFetchState(
        [],
        `/api/v1/invitations/sent`,
        jwt
    );






    const invitationList =
        invitationsReceived.map((a) => {
            return (
                <tr key={a.id}>
                    <td className="text-center">{a.discriminator}</td>
                    <td className="text-center">{a.playerSource.user.username}</td>
                    <td className="text-center">
                        <Button outline color="success" 
                            >
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
                    <td className="text-center">{a.isAccepted===false ? "Pendiente" : "Aceptada"}</td>

                    <td className="text-center">
                        <Button outline color="danger"
                            onClick={() =>
                                deleteFromList(
                                    `/api/v1/invitations/${a.id}`,
                                    a.id,
                                    [invitationSent, setInvitationsSent],
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
                                <th className="text-center">Actions</th>
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