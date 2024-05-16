import {useState} from "react";
import {Accordion, Collapse, Table} from "react-bootstrap";
import Nav from "react-bootstrap/Nav";
import style from "../../../../style/main.module.scss";

interface ImportErrorsProps {
  errors?: any[]
  errorsCount?: number
}

export default function ImportErrors({errors, errorsCount}: ImportErrorsProps) {
  const [errorsShown, setErrorsShown] = useState(false)

  if (!errors || !errorsCount || errorsCount == 0)
    return null

  return (
    <>
      <button className={"btn btn-danger mb-2"} onClick={() => setErrorsShown(prev => !prev)}>
        {errorsCount} błędów
      </button>
      <Collapse in={errorsShown} className={"mt-2 mb-2"}>
        <Accordion flush>
          {errors?.map((error, index) => <ImportError error={error} index={index} key={index}/>)}
        </Accordion>
      </Collapse>
    </>
  )
}

function ImportError({error, index}: { error: any, index: number }) {
  const [detailsMode, setDetailsMode] = useState<"TABLE" | "RAW">("TABLE")

  return (
    <Accordion.Item eventKey={error.vehicleId ?? index} key={error.vehicleId ?? index}>
      <Accordion.Header className={style.cutText}>{error.vehicleId}: {error.errorMessage}</Accordion.Header>
      <Accordion.Body>
        <Nav justify variant="tabs" defaultActiveKey="TABLE" className={"mt-3"}
             onSelect={(key) => setDetailsMode(key as any)}>
          <Nav.Item>
            <Nav.Link eventKey="TABLE">Tabela</Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link eventKey="RAW">Surowe dane</Nav.Link>
          </Nav.Item>
        </Nav>
        {detailsMode === "TABLE" ?
          <Table bordered striped>
            <thead>
            <tr>
              <th>Kolumna</th>
              <th>Wartość</th>
            </tr>
            </thead>
            <tbody>
            <tr>
              <td>pojazd_id</td>
              <td>{error.columnData['pojazd_id']}</td>
            </tr>
            {Object.entries(error.columnData).map(([key, value]) => (
              key == "pojazd_id" ? null :
                <tr key={key}>
                  <td>{key}</td>
                  <td>{value as string}</td>
                </tr>
            ))}
            </tbody>
          </Table>
          : error.line}
      </Accordion.Body>
    </Accordion.Item>
  )
}