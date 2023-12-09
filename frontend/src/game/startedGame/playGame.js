import {
    Table, Button
} from "reactstrap";

import getIdFromUrl from "../../util/getIdFromUrl";
import tokenService from "../../services/token.service";
import useFetchState from "../../util/useFetchState";
import { Link } from "react-router-dom";
import deleteFromList from "../../util/deleteFromList";
import { useState } from "react";
import getErrorModal from "../../util/getErrorModal";


const jwt = tokenService.getLocalAccessToken();
export default function GameList() {
    const name = getIdFromUrl(3);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [alerts, setAlerts] = useState([]);
    const [hex, setHexs] = useFetchState(
        [],
        `/api/v1/gameBoard/${name}`,
        jwt
    );

    const hexList =
        hex.map((a) => {
            return (
                <tr key={a.id}>
                    <td className="text-center">{a.puntos}</td>
                    <td className="text-center">{a.position}</td>
                </tr>
            );
        });

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div>
            <div className="admin-page-container">
                <h1 className="text-center">Game Board Hexs</h1>
                <div>
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                            <tr>
                                <th className="text-center">Value</th>
                                <th className="text-center">Position</th>
                            </tr>
                        </thead>
                        <tbody>{hexList}</tbody>
                    </Table>
                </div>
            </div>
        </div>
    );
}