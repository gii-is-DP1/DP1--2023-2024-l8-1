import {
    Table, Button
} from "reactstrap";

import tokenService from "../../services/token.service";
import useFetchState from "../../util/useFetchState";
import { Link } from "react-router-dom";
import deleteFromList from "../../util/deleteFromList";
import { useState } from "react";
import getErrorModal from "../../util/getErrorModal";
import { useNavigate } from 'react-router-dom';

const imgnotfound = "https://cf.geekdo-images.com/K34Z34C_Vltb6iZz4DDH3g__opengraph/img/flVso2n16F7OKvS_RkiVfeEckTU=/0x0:1463x768/fit-in/1200x630/filters:strip_icc()/pic2644229.jpg";
const jwt = tokenService.getLocalAccessToken();
export default function GameList() {
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [alerts, setAlerts] = useState([]);
    const navigate = useNavigate();
    const loggedUser = tokenService.getUser()
    const [games, setGames] = useFetchState(
        [],
        `/api/v1/game/publicas`,
        jwt
    );

    const [friendGames, setFriendGames] = useFetchState(
        [],
        `/api/v1/game/friendGames`,
        jwt
    );

    const [playerGames, setPlayerGames] = useFetchState(
        [],
        `/api/v1/game/playerCurrentGames`,
        jwt
    );

    const handleSpectate = (name) => {
        fetch(`/api/v1/players/startSpectating/${loggedUser.username}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
        });

        navigate('../game/play/' + name);
    }

    const handleJoin = (name) => {
        console.log(loggedUser)
        fetch(`/api/v1/game/join/${name}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
        });

        navigate('../game/lobby/' + name);
    }

    const gameList =
        games.map((a) => {
            let lobby = 0;
            const userList =
                a.players.map((a) => {
                    const card = a.user
                    return (card)
                })
            if (a.players.length === 2 && a.state !== "LOBBY" && a.state !== "OVER") {
                lobby = 1
            } else if (a.players.length === 2 && a.state === "LOBBY") {
                lobby = 2
            } else if ((userList.includes(loggedUser)) || a.host.user.username === loggedUser.username) {
                lobby = 2
            }

            return (
                <tr key={a.id}>
                    <td className="text-center">{a.name}</td>
                    <td className="text-center">{a.host.user.username}</td>
                    <td className="text-center">
                        <img src={a.badgeImage ? a.badgeImage : imgnotfound} alt={a.name} width="50px" />
                    </td>
                    <td className="text-center">{a.startTime}</td>
                    <td className="text-center"> {a.state} </td>
                    {lobby === 0 && <td className="text-center">
                        <Button outline color="warning" onClick={() => handleJoin(a.name)} >
                            Join
                        </Button>
                    </td>}
                    {lobby === 1 && <td className="text-center">
                        <Button outline color="warning" onClick={() => handleSpectate(a.name)}>
                            Spectate
                        </Button>
                    </td>}
                    {lobby === 2 && <td className="text-center">
                        <Button outline color="warning" onClick={() => navigate('../game/lobby/' + a.name)}>
                            Lobby
                        </Button>
                    </td>}
                    {loggedUser.username === a.host.user.username && <td className="text-center">
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
                    </td>}
                </tr>
            );
        });

    const playerGamesList =
        playerGames.map((a) => {
            return (
                <tr key={a.id}>
                    <td className="text-center">{a.name}</td>
                    <td className="text-center">{a.host.user.username}</td>
                    <td className="text-center">
                        <img src={a.badgeImage ? a.badgeImage : imgnotfound} alt={a.name} width="50px" />
                    </td>
                    <td className="text-center">{a.startTime}</td>
                    <td className="text-center"> {a.state} </td>
                    <td className="text-center">
                        <Button outline color="warning" onClick={() => navigate('../game/lobby/' + a.name)}>
                            Lobby
                        </Button>
                    </td>
                    {loggedUser.username === a.host.user.username && <td className="text-center">
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
                    </td>}
                </tr>
            );
        });


    const friendGamesList =
        friendGames.map((a) => {
            return (
                <tr key={a.id}>
                    <td className="text-center">{a.name}</td>
                    <td className="text-center">{a.host.user.username}</td>
                    <td className="text-center">
                        <img src={a.badgeImage ? a.badgeImage : imgnotfound} alt={a.name} width="50px" />
                    </td>
                    <td className="text-center">{a.startTime}</td>
                    <td className="text-center"> {a.state} </td>
                    <td className="text-center">
                        <Button outline color="warning" onClick={() => handleSpectate(a.name)}>
                            Spectate
                        </Button>
                    </td>
                </tr>
            );
        });

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div>
            <div className="admin-page-container">
                <h1 className="text-center" style={{ marginTop: "20px" }}>Public Games</h1>
                <div>
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                            <tr>
                                <th className="text-center">Nombre</th>
                                <th className="text-center">Host</th>
                                <th className="text-center">Image</th>
                                <th className="text-center">Start Date</th>
                                <th className="text-center">Game State</th>
                                <th className="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>{gameList}</tbody>
                    </Table>
                </div>
            </div>
            <div className="admin-page-container">
                <h1 className="text-center" style={{ marginTop: "20px" }}>Your Games</h1>
                <div>
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                            <tr>
                                <th className="text-center">Nombre</th>
                                <th className="text-center">Host</th>
                                <th className="text-center">Image</th>
                                <th className="text-center">Start Date</th>
                                <th className="text-center">Game State</th>
                                <th className="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>{playerGamesList}</tbody>
                    </Table>
                </div>
            </div>
            <div className="admin-page-container">
                <h1 className="text-center" style={{ marginTop: "20px" }}>Friends Games Started</h1>
                <div>
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                            <tr>
                                <th className="text-center">Nombre</th>
                                <th className="text-center">Host</th>
                                <th className="text-center">Image</th>
                                <th className="text-center">Start Date</th>
                                <th className="text-center">Game State</th>
                                <th className="text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody>{friendGamesList}</tbody>
                    </Table>
                    <Button outline color="success" >
                        <Link
                            to={'/game/new'} className="btn sm"
                            style={{ textDecoration: "none" }}>Create new Game</Link>
                    </Button>
                </div>
            </div>

        </div>
    );
}