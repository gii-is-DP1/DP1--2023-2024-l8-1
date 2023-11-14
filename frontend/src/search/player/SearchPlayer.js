import React, { useState } from "react";
import { FaSearch } from "react-icons/fa";
import tokenService from "../../services/token.service";

import "../SearchBar.css"; 

export const SearchPlayer = ({ setResults, onSelectPlayer }) => {
    const [input, setInput] = useState("");
    const [isSearchOpen, setIsSearchOpen] = useState(true); // Nuevo estado para controlar la apertura/cierre

    const jwt = tokenService.getLocalAccessToken();

    const fetchData = (value) => {
        fetch("/api/v1/players", {
            method: "GET",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
        })
        .then((response) => response.json())
        .then((json) => {
            const results = Object.values(json).filter((player) => {
                return (
                    value &&
                    player &&
                    player.user.username &&
                    player.user.username.toLowerCase().includes(value)
                );
            });
            setResults(results);
        });
    };

    const handleChange = (value) => {
        setInput(value);
        fetchData(value);
    };

    const handleSelectPlayer = (selectedPlayer) => {
        setIsSearchOpen(false); // Cerrar el cuadro de búsqueda
        onSelectPlayer(selectedPlayer);
    };

    return (
        <div className={`input-wrapper ${isSearchOpen ? 'open' : 'closed'}`}>
            <FaSearch id="search-icon" />
            <input
                placeholder="Buscar jugadores..."
                value={input}
                onChange={(e) => handleChange(e.target.value)}
            />
        </div>
    );
};
