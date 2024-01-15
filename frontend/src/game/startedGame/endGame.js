import {
    Table, Button
} from "reactstrap";

import tokenService from "../../services/token.service";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchState from "../../util/useFetchState";
import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import "./tablero.css"

const jwt = tokenService.getLocalAccessToken();
const corona = "https://c0.klipartz.com/pngpicture/549/886/gratis-png-corona-stock-photography-corona-de-oro.png";
export default function EndGame() {
    const name = getIdFromUrl(3);
    const navigate = useNavigate();

    const [sortedPlayers, setSortedPlayers] = useFetchState (
        [],
        `/api/v1/game/getWinner/${name}`,
        jwt
    );

    const winner = sortedPlayers[0];

    const sortedPlayersList =
        sortedPlayers.map((p) => {
            return (
                <tr>
                    {p.user.username === winner.user.username && <td className="text-center">
                        <img src={corona} alt={p.name} width="50px" />
                    </td>}
                    {p.user.username !== winner.user.username && <td className="text-center">
                        ---
                    </td>}
                    <td className="text-center">{p.user.username}</td>
                    <td className="text-center">{p.score}</td>
                </tr>
            );
        });

    return (
        <div>
            <div className="admin-page-container">
                <h1 className="text-center">Game's Players</h1>
                <div>
                <div>
                    <Table aria-label="achievements" className="mt-4">
                        <thead>
                            <tr>
                                <th className="text-center">Winner</th>
                                <th className="text-center">Name</th>
                                <th className="text-center">Final Score</th>
                            </tr>
                        </thead>
                        <tbody>{sortedPlayersList}</tbody>
                    </Table>
                </div>
                <div className="buttonStyles">
                <Button outline color="warning" onClick={() => navigate('../game/')}>
                        Home
                    </Button>
                </div>
            </div>
        </div>
        </div>

    )

}