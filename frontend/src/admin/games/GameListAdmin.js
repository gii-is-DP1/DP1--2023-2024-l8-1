import {
    Table, Button
} from "reactstrap";

import tokenService from "../../services/token.service";
import useIntervalFetchState from "../../util/useIntervalFetchState";
import { Link } from "react-router-dom";
import deleteFromList from "../../util/deleteFromList";
import { useState } from "react";
import getErrorModal from "../../util/getErrorModal";

const imgnotfound = "https://cf.geekdo-images.com/K34Z34C_Vltb6iZz4DDH3g__opengraph/img/flVso2n16F7OKvS_RkiVfeEckTU=/0x0:1463x768/fit-in/1200x630/filters:strip_icc()/pic2644229.jpg";
const jwt = tokenService.getLocalAccessToken();
export default function GameListAdmin() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [alerts, setAlerts] = useState([]);
    const [games, setGames] = useIntervalFetchState(
        [],
        `/api/v1/game`,
        jwt
    );

    const gameList =
        games.map((a) => {
            const firstPlayerName = a.players.length > 0 ? a.players[0].user.username : "N/A";
            const secondPlayerName = a.players.length > 1 ? a.players[1].user.username : "N/A";
            return (
                <tr key={a.id}>
                    <td className="text-center">{a.name}</td>
                    <td className="text-center">
                        <img src={a.badgeImage ? a.badgeImage : imgnotfound} alt={a.name} width="50px" />
                    </td>
                    <td className="text-center">{a.startTime}</td>
                    <td className="text-center"> {a.state} </td>
                    <td className="text-center"> {a.host.user.username} </td>
                    <td className="text-center">
                    {firstPlayerName} <br /> {secondPlayerName}
                    </td>
                    <td className="text-center">
                        <Button outline color="danger"
                            onClick={() =>
                                deleteFromList(
                                    `/api/v1/game/${a.id}`,
                                    a.id,
                                    [games, setGames],
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
                <h1 className="text-center">Games</h1>
                <div>
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                            <tr>
                                <th className="text-center">Name</th>
                                <th className="text-center">Image</th>
                                <th className="text-center">Start Date</th>
                                <th className="text-center">Game State</th>
                                <th className="text-center">Host</th>
                                <th className="text-center">Player</th>
                                <th className="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>{gameList}</tbody>
                    </Table>
                </div>
            </div>
        </div>
    );
}