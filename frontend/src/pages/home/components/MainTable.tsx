import style from "../Home.module.scss";
import {Table} from "react-bootstrap";
import {formatNumber} from "../../../util/utils.ts";
import {INDICATORS} from "./MapAndTable.tsx";
import {MergedData, VoivodeshipData, YearData} from "../hooks/HomeDataContext.tsx";
import {useMemo, useState} from "react";
import {FaSort, FaSortDown, FaSortUp} from "react-icons/fa6";

interface TableProps {
  data: YearData | null | undefined
  selectedIndicator: string | null
  hoveredVoivodeship: string | null;
  setHoveredVoivodeship: (voivodeship: string | null) => void;
}

type SortColumn =
  "voivodeship"
  | "registrations"
  | "deregistrations"
  | "NO2"
  | "NOx"
  | "PM2.5"
  | "Pb(PM10)"
  | "SO2"
  | "none";
type SortOrder = "asc" | "desc" | "none";


export default function MainTable({
                                    data: finalData,
                                    selectedIndicator,
                                    setHoveredVoivodeship,
                                    hoveredVoivodeship
                                  }: TableProps) {
  const TABLE_HEADERS = ["registrations", "deregistrations", ...INDICATORS]
  const [sortOrder, setSortOrder] = useState<SortOrder>("none")
  const [sortColumn, setSortColumn] = useState<SortColumn>("none")

  /**
   * Sorts voivodeships based on the current sort order and column and returns an array of voivodeship names.
   */
  const sortedVoivodeshipNames = useMemo(() => {
    if (!finalData) return []

    if (sortOrder === "none" || sortColumn === "none") return Object.keys(finalData)

    const entries = Object.entries(finalData)

    const sortedEntries = entries.sort((a, b) => {
      const aVal = sortColumn == "voivodeship" ? a[0] : a[1][sortColumn as keyof VoivodeshipData]
      const bVal = sortColumn == "voivodeship" ? b[0] : b[1][sortColumn as keyof VoivodeshipData]

      const compare = aVal == undefined ?
        1 :
        bVal == undefined ?
          -1 :
          typeof aVal === "number" && typeof bVal === "number" ?
            aVal - bVal :
            aVal.toString().localeCompare(bVal.toString())

      return sortOrder === "asc" ? compare : -compare
    })

    return sortedEntries.map(([key]) => key)
  }, [finalData, sortOrder, sortColumn])

  /**
   * Returns the sort icon for the given column.
   * @param column The column to get the sort icon for.
   */
  const getSortIcon = (column: SortColumn) => {
    if (column !== sortColumn || sortOrder === "none")
      return <FaSort/>
    return sortOrder === "asc" ? <FaSortUp/> : <FaSortDown/>
  }

  /**
   * Handles the click event on the table header and updates the sort order and column accordingly.
   * @param column The column that was clicked.
   */
  const onHeaderClick = (column: SortColumn) => {
    if (column === sortColumn) {
      if (sortOrder === "asc") {
        setSortOrder("desc")
      } else if (sortOrder === "desc") {
        setSortOrder("none")
      } else {
        setSortOrder("asc")
      }

      return
    }

    setSortColumn(column)
    setSortOrder("asc")
  }

  return (
    <div className={`col-12 col-xxl-7 ${style.table}`}>
      <div className={style.scrollableTableWrapper}>
        <Table bordered striped={finalData ? true : undefined}>
          <thead>
          <tr>
            <th onClick={() => onHeaderClick("voivodeship")}>
              Województwo {getSortIcon("voivodeship")}
            </th>
            <th onClick={() => onHeaderClick("registrations")}>
              Rejestracje {getSortIcon("registrations")}
            </th>
            <th onClick={() => onHeaderClick("deregistrations")}>
              Wyrejestrowania {getSortIcon("deregistrations")}
            </th>
            {INDICATORS.map((indicator) => (
              <th key={indicator} onClick={() => onHeaderClick(indicator as SortColumn)}>
                {indicator}
                {getSortIcon(indicator as SortColumn)}
              </th>
            ))}
          </tr>
          </thead>
          <tbody>
          {finalData
            ? (sortedVoivodeshipNames as Array<keyof MergedData>)
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
      {Array.from({length: 3 + INDICATORS.length}, (_, j) => (
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