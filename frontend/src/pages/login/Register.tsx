import {title} from "../../util/title.ts";
import Container from "react-bootstrap/Container";
import {Button, Form} from "react-bootstrap";
import {FormEvent, useEffect, useRef, useState} from "react";
import {getApi} from "../../axios/axios.ts";
import {useDispatch, useSelector} from "react-redux";
import {State} from "../../store";
import {Link, useNavigate} from "react-router-dom";
import {userActions} from "../../store/userSlice.ts";
import {AxiosResponse} from "axios";

interface ErrorState {
  username?: string,
  password?: string,
  passwordConfirmation?: string,
  message?: string,
}

export default function Register() {
  title("Rejestracja")

  const usernameRef = useRef<HTMLInputElement>(null);
  const passwordRef = useRef<HTMLInputElement>(null);
  const passwordConfirmationRef = useRef<HTMLInputElement>(null);
  const [error, setError] = useState<ErrorState>({})

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
    const [username, password, passwordConfirmation] = [usernameRef.current?.value, passwordRef.current?.value, passwordConfirmationRef.current?.value];

    const errors: ErrorState = {}
    if (!username || !password || !passwordConfirmation) {
      errors.message = "Wszystkie pola są wymagane"
    }

    if (password && password !== passwordConfirmation) {
      errors.message = "Hasła nie są takie same"
    }

    if (password && password.length < 4) {
      errors.password = "Hasło musi mieć co najmniej 4 znaki"
    }

    if (username && username.length < 4) {
      errors.username = "Nazwa użytkownika musi mieć co najmniej 4 znaki"
    }

    setError(errors)
    if (Object.keys(errors).length) {
      return
    }

    getApi().post("/auth/register", {username, password, passwordConfirmation})
      .then((response: AxiosResponse) => {
        if (response.status < 300) {
          setError({})
          dispatch(userActions.setUserData(response.data))
          return
        }

        if (response.data.message === "Validation failed") {
          const errors: ErrorState = {}
          Object.keys(response.data.errors).forEach((key) => {
            errors[key as keyof ErrorState] = response.data.errors[key][0]
          })
          setError(errors)
        } else {
          setError({message: response.data.message ?? "Wystąpił błąd serwera"})
        }
      })
      .catch((e: any) => {
        setError({message: "Wystąpił błąd serwera"})
        console.error(e)
      })
  }

  return (
    <Container className="mt-5 col-12 col-md-6 col-xl-5 col-xxl-3 p-4 rounded bg-light">
      <h2 className="text-center">Rejestracja</h2>
      <Form className="mt-4" onSubmit={handleSubmit}>
        <Form.Group controlId="username">
          <Form.Label>Nazwa użytkownika</Form.Label>
          <Form.Control type="text" placeholder="nazwa" ref={usernameRef}/>
          <Form.Text className="text-danger text-center d-block mt-2">
            {error.username}
          </Form.Text>
        </Form.Group>

        <Form.Group controlId="password" className={"mt-2"}>
          <Form.Label>Hasło</Form.Label>
          <Form.Control type="password" placeholder="hasło" ref={passwordRef}/>
          <Form.Text className="text-danger text-center d-block mt-2">
            {error.password}
          </Form.Text>
        </Form.Group>

        <Form.Group controlId="passwordConfirmation" className={"mt-2"}>
          <Form.Label>Potwierdzenie hasło</Form.Label>
          <Form.Control type="password" placeholder="powtórz hasło" ref={passwordConfirmationRef}/>
          <Form.Text className="text-danger text-center d-block mt-2">
            {error.passwordConfirmation}
          </Form.Text>
        </Form.Group>

        <Form.Text className="text-danger text-center d-block mt-2">
          {error.message}
        </Form.Text>
        <Button variant="primary" type="submit" className={"d-block mx-auto mt-3"}>
          Utwórz konto
        </Button>
        <Form.Text className="text-center mt-3 w-100 d-block">
          Masz konto? <Link to="/login">Zaloguj się</Link>
        </Form.Text>
      </Form>
    </Container>
  );
}