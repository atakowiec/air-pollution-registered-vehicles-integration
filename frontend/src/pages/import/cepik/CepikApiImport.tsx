import Container from "react-bootstrap/Container";
import { Form } from "react-bootstrap";
import { ChangeEvent, useState } from "react";
import { MainNavbar } from "../../../components/MainNavbar.tsx";
import ImportProgressInfo from "./progress/ImportProgressInfo.tsx";
import { title } from "../../../util/title.ts";
import { getApi } from "../../../axios/axios";

export default function CepikApiImport() {
  title("Import danych z API");

  const [apiLink, setApiLink] = useState<string>("");
  const [status, setStatus] = useState("");
  const [progress, setProgress] = useState(null);

  const onLinkChange = (event: ChangeEvent<HTMLInputElement>) => {
    setApiLink(event.target.value);
    setStatus("");
  };

  const onApiImport = () => {
    if (!apiLink) {
      setStatus("Nie podano linku do API.");
      return;
    }

    setStatus("Pobieranie danych z API...");

    getApi()
      .post("/vehicles/import/api", { apiUrl: apiLink }) 
      .then((response) => {
        if (response.status === 200) {
          const data = response.data;
          setStatus("Dane zostały przesłane.");
          setProgress(data);
        } else {
          setStatus("Wystąpił błąd podczas importu danych.");
        }
      })
      .catch(() => {
        setStatus("Wystąpił błąd podczas importu danych.");
      });
  };

  return (
    <>
      <MainNavbar />
      <Container className={"bg-light rounded text-center p-4 mt-5 col-12 col-md-8 col-xl-6 col-xxl-5"}>
        <h3>Import danych dotyczących zarejestrowanych samochodów</h3>
        <h5>z API</h5>
        <Form>
          <Form.Control
            type="text"
            placeholder="Podaj link do API"
            value={apiLink}
            onChange={onLinkChange}
            className={"mt-5 w-75 mx-auto"}
          />
          <button className={"btn btn-primary mt-3"} type={"button"} onClick={onApiImport}>
            Prześlij
          </button>
        </Form>
        {status && <p className={"mt-3"}>{status}</p>}
      </Container>
      {progress && <ImportProgressInfo progress={progress} />}
    </>
  );
}
