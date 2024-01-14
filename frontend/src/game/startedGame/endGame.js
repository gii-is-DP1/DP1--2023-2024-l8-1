import {
    Table, Button
} from "reactstrap";

import tokenService from "../../services/token.service";
import getIdFromUrl from "../../util/getIdFromUrl";
import useFetchState from "../../util/useFetchState";
import { useState } from "react";

const jwt = tokenService.getLocalAccessToken();

export default function EndGame() {
    const name = getIdFromUrl(3);

    const [sortedPlayers, setSortedPlayers] = useFetchState (
        [],
        `/api/v1/game/getWinner/${name}`,
        jwt
    );

    const sortedPlayersList =
        sortedPlayers.map((p) => {
            return (
                <tr>
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
                                <th className="text-center">Name</th>
                                <th className="text-center">Final Score</th>
                            </tr>
                        </thead>
                        <tbody>{sortedPlayersList}</tbody>
                    </Table>
                </div>
            </div>
        </div>
        </div>

    )

}