import "../../static/css/auth/authButton.css";
import "../../static/css/auth/authPage.css";
import tokenService from "../../services/token.service";
import FormGenerator from "../../components/formGenerator/formGenerator";
import { useRef } from "react";
import { registerFormPlayerInputs } from "./form/registerFormPlayerInputs";

export default function Register() {
  let authority = "Player";
  const registerFormRef = useRef();

  function handleSubmit({ values }) {
    if (!registerFormRef.current.validate()) return;

    const request = values;
    request["authority"] = authority;

    fetch("/api/v1/auth/signup", {
      headers: { "Content-Type": "application/json" },
      method: "POST",
      body: JSON.stringify(request),
    })
      .then(function (response) {
        if (response.status === 200) {
          const loginRequest = {
            username: request.username,
            password: request.password,
          };

          fetch("/api/v1/auth/signin", {
            headers: { "Content-Type": "application/json" },
            method: "POST",
            body: JSON.stringify(loginRequest),
          })
            .then(function (response) {
              if (response.status === 200) {
                return response.json();
              } else {
                throw new Error(response.statusText); // Manejar errores
              }
            })
            .then(function (data) {
              tokenService.setUser(data);
              tokenService.updateLocalAccessToken(data.token);
              window.location.href = "/";
            })
            .catch((error) => {
              alert("Error during login: " + error.message);
            });
        } else {
          throw new Error(response.statusText); // Manejar errores
        }
      })
      .catch((error) => {
        alert("Error during registration: " + error.message);
      });
  }

  return (
    <div className="auth-page-container">
      <h1>Register</h1>
      <div className="auth-form-container">
        <FormGenerator
          ref={registerFormRef}
          inputs={registerFormPlayerInputs}
          onSubmit={handleSubmit}
          numberOfColumns={1}
          listenEnterKey
          buttonText="Save"
          buttonClassName="auth-button"
        />
      </div>
    </div>
  );
}
