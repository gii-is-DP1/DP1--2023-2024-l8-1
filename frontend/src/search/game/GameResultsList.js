import React from "react";
import "../SearchResultsList.css";
import { GameResult } from "./GameResult";

export const GameResultsList = ({ results, onSelectGame }) => {
    return (
        <div className="results-list">
            {results.map((result, id) => (
                <GameResult
                    result={result}
                    key={id}
                    onSelectGame={onSelectGame}
                />
            ))}
        </div>
    );
};
