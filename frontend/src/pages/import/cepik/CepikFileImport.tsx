import Container from "react-bootstrap/Container";
import { Form } from "react-bootstrap";
import { ChangeEvent, useState } from "react";
import { getApi } from "../../../axios/axios.ts";
import { MainNavbar } from "../../../components/MainNavbar.tsx";

interface ImportResponse {
  [key: string]: number;
}

export default function CepikFileImport() {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [result, setResult] = useState<ImportResponse>({});
  const [status, setStatus] = useState("");

  const onFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      setSelectedFile(event.target.files[0]);
      setStatus("");
    }
  };

  const onFileUpload = () => {
    if (!selectedFile) {
      setStatus("Nie wybrano pliku.");
      return;
    }

    if (!selectedFile.name.endsWith(".csv")) {
      setStatus("Wybrano zły format pliku. Wymagany format to CSV.");
      return;
    }

    const formData = new FormData();
    formData.append("file", selectedFile, selectedFile.name);

    setResult({});
    setStatus("Trwa importowanie danych... To może potrwać kilka minut");

    getApi()
      .post("/vehicles/import/csv", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      })
      .then((response) => {
        console.log('Upload response', response); // Dodajemy logowanie tutaj
        if (response.status === 200) { // Zakładamy, że 200 oznacza sukces
          setResult(response.data);
          setStatus("Plik został przesłany.");
        } else {
          setStatus("Wystąpił błąd podczas zapisywania do bazy danych.");
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
        <h5>z pliku CSV</h5>
        <Form>
          <Form.Control type="file" accept=".csv" onChange={onFileChange} className={"mt-5 w-75 mx-auto"} />
          <button className={"btn btn-primary mt-3"} type={"button"} onClick={onFileUpload}>Prześlij</button>
        </Form>
        {status && <p className={"mt-3"}>{status}</p>}

        {Object.keys(result).length > 0 && (
          <div className={"mt-3"}>
            <h5>Wynik importu:</h5>
            {Object.entries(result).map(([key, value]) => (
              <div key={key}><b>{key}</b>: {value} wyników</div>
            ))}
          </div>
        )}
      </Container>
    </>
  );
}
