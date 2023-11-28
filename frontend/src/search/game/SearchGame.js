import React, { useState } from "react";
import { FaSearch } from "react-icons/fa";
import tokenService from "../../services/token.service";

import "../SearchBar.css"; 

export const SearchGame= ({ setResults, onSelectGame}) => {
    const [input, setInput] = useState("");
    const [isSearchOpen, setIsSearchOpen] = useState(true); // Nuevo estado para controlar la apertura/cierre

    const jwt = tokenService.getLocalAccessToken();

    const fetchData = (value) => {
        fetch("/api/v1/game", {
            method: "GET",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
        })
        .then((response) => response.json())
        .then((json) => {
            const results = Object.values(json).filter((game => {
                return (
                    value &&
                    game &&
                    game.name &&
                    game.name.toLowerCase().includes(value)
                );
            }));
            setResults(results);
        });
    };

    const handleChange = (value) => {
        setInput(value);
        fetchData(value);
    };

    const handleSelectGame = (selectedGame) => {
        setIsSearchOpen(false); // Cerrar el cuadro de búsqueda
        onSelectGame(selectedGame);
    };

    return (
        <div className={`input-wrapper ${isSearchOpen ? 'open' : 'closed'}`}>
            <FaSearch id="search-icon" />
            <input
                placeholder="Buscar partidas..."
                value={input}
                onChange={(e) => handleChange(e.target.value)}
            />
        </div>
    );
};
