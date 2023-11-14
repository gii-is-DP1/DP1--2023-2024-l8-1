import React from "react";
import "../SearchResultsList.css";
import { PlayerResult } from "./PlayerResult";

export const PlayerResultsList = ({ results, onSelectPlayer }) => {
    return (
        <div className="results-list">
            {results.map((result, id) => (
                <PlayerResult
                    result={result}
                    key={id}
                    onSelectPlayer={onSelectPlayer}
                />
            ))}
        </div>
    );
};
