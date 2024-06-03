import style from "../Home.module.scss";
import {Table} from "react-bootstrap";
import {formatNumber} from "../../../util/utils.ts";
import {INDICATORS} from "./MapAndTable.tsx";
import {MergedData, VoivodeshipData, YearData} from "../hooks/HomeDataContext.tsx";

interface TableProps {
  data: YearData | null | undefined
  selectedIndicator: string | null
  hoveredVoivodeship: string | null;
  setHoveredVoivodeship: (voivodeship: string | null) => void;
}


export default function MainTable({data: finalData, selectedIndicator, setHoveredVoivodeship, hoveredVoivodeship}: TableProps) {
  const TABLE_HEADERS = ["registrations", "deregistrations", ...INDICATORS]

  return (
    <div className={`col-12 col-xxl-7 ${style.table}`}>
      <div className={style.scrollableTableWrapper}>
        <Table bordered striped={finalData ? true : undefined}>
          <thead>
          <tr>
            <th>Województwo</th>
            <th>Rejestracje</th>
            <th>Wyrejestrowania</th>
            {INDICATORS.map((indicator) => (
              <th key={indicator}>
                {indicator}
              </th>
            ))}
          </tr>
          </thead>
          <tbody>
          {finalData
            ? (Object.keys(finalData) as Array<keyof MergedData>)
              .map((voivodeshipName) => (
                <tr key={voivodeshipName}
                    className={hoveredVoivodeship === voivodeshipName.toString() ? style.hoveredRow : undefined}
                    onMouseEnter={() => setHoveredVoivodeship(voivodeshipName.toString())}
                    onMouseLeave={() => setHoveredVoivodeship(null)}>
                  <td>{voivodeshipName}</td>
                  {TABLE_HEADERS.map((indicator) => (
                    <td key={indicator} style={{fontWeight: indicator == selectedIndicator ? "bold" : undefined}}>
                      {formatNumber(finalData[voivodeshipName][indicator as keyof VoivodeshipData] as number)}
                    </td>
                  ))}
                </tr>
              ))
            : Array.from({length: 16}, (_, i) => (
              <LoadingRow key={i} i={i}/>
            ))}
          </tbody>
        </Table>
      </div>
    </div>
  )
}

function LoadingRow({i}: { i: number }) {
  return (
    <tr>
      {Array.from({length: 2 + INDICATORS.length}, (_, j) => (
        <td
          key={`td-${j}`}
          className={style.loadingRow}
          style={{animationDelay: `${i * 0.1 + j * 0.1}s`}}
        >
          ...
        </td>
      ))}
    </tr>
  );
}