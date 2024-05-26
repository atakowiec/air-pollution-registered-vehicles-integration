import style from "../Home.module.scss";
import PolandMap from "./PolandMap.tsx";
import {Row, Table} from "react-bootstrap";
import {formatNumber} from "../../../util/utils.ts";
import ChartStatusOverlay, {PropsWithApiData} from "./ChartStatusOverlay.tsx";

export default function MapAndTable({apiData}: PropsWithApiData) {
  return (
    <Row>
      <div className={`text-center col-12 col-xxl-5 ${style.map} position-relative`}>
        <ChartStatusOverlay apiData={apiData} backgroundType={"none"}/>
        <PolandMap apiData={apiData}/>
      </div>
      <div className={`col-12 col-xxl-7 ${style.table}`}>
        <Table bordered striped={apiData.data ? true : undefined}>
          <thead>
          <tr>
            <th>Województwo</th>
            <th>Liczba pojazdów</th>
            <th>Jakies inne dane</th>
          </tr>
          </thead>
          <tbody>
          {apiData.data ? Object.keys(apiData.data).map((key) => (
              <tr key={key}>
                <td>{key}</td>
                <td>{formatNumber(apiData.data[key])}</td>
                <td>...</td>
              </tr>
            ))
            : Array.from({length: 10}, (_, i) => <LoadingRow key={i} i={i}/>)
          }
          </tbody>
        </Table>
      </div>
    </Row>
  )
}

function LoadingRow({i}: { i: number }) {
  return (
    <tr>
      <td className={style.loadingRow} style={{animationDelay: `${i * 0.1}s`}}>...</td>
      <td className={style.loadingRow} style={{animationDelay: `${i * 0.1 + 0.1}s`}}>...</td>
      <td className={style.loadingRow} style={{animationDelay: `${i * 0.1 + 0.2}s`}}>...</td>
    </tr>
  )
}