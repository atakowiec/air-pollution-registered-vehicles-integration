import {title} from "../../util/title.ts";
import Container from "react-bootstrap/Container";
import {Button, Form} from "react-bootstrap";
import {FormEvent, useEffect, useRef, useState} from "react";
import {getApi} from "../../axios/axios.ts";
import {useDispatch, useSelector} from "react-redux";
import {State} from "../../store";
import {Link, useNavigate} from "react-router-dom";
import {userActions} from "../../store/userSlice.ts";

export default function Login() {
  title("Logowanie");

  const usernameRef = useRef<HTMLInputElement>(null);
  const passwordRef = useRef<HTMLInputElement>(null);
  const [error, setError] = useState<string | null>(null)

  const navigate = useNavigate()
  const dispatch = useDispatch()
  const user = useSelector((state: State) => state.user)

  useEffect(() => {
    if (user.isLogged) {
      navigate("/")
    }
  }, [user.isLogged]);

  function handleSubmit(event: FormEvent) {
    event.preventDefault();
    const [username, password] = [usernameRef.current?.value, passwordRef.current?.value];

    if (!username || !password) {
      setError("Wypełnij wszystkie pola")
      return
    }

    getApi().post("/auth/login", {username, password})
      .then((response) => {
        if (response.status === 200) {
          setError(null)
          dispatch(userActions.setUserData(response.data))
          return
        }

        if (response.data.message === "Validation failed") {
          setError("Niepoprawne dane logowania")
        } else {
          setError(response.data.message ?? "Wystąpił błąd serwera")
        }
      })
      .catch((e) => {
        setError("Wystąpił błąd serwera")
        console.error(e)
      })
  }

  return (
    <Container className="mt-5 col-12 col-md-6 col-xl-5 col-xxl-3 p-4 rounded bg-light">
      <h2 className="text-center">Logowanie</h2>
      <Form className="mt-4" onSubmit={handleSubmit}>
        <Form.Group controlId="username">
          <Form.Label>Nazwa użytkownika</Form.Label>
          <Form.Control type="text" placeholder="nazwa" ref={usernameRef}/>
        </Form.Group>

        <Form.Group controlId="password" className={"mt-2"}>
          <Form.Label>Hasło</Form.Label>
          <Form.Control type="password" placeholder="hasło" ref={passwordRef}/>
        </Form.Group>

        <Form.Text className="text-danger text-center d-block mt-2">
          {error}
        </Form.Text>
        <Button variant="primary" type="submit" className={"d-block mx-auto mt-3"}>
          Zaloguj
        </Button>
        <Form.Text className="text-center mt-3 w-100 d-block">
          Nie masz konta? <Link to="/register">Zarejestruj się</Link>
        </Form.Text>
      </Form>
    </Container>
  );
}