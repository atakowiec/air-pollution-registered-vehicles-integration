import {CartesianGrid, Legend, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis} from "recharts";
import {useCumulativeData, useMergedData} from "../hooks/homeHooks.ts";
import {useMemo, useState} from "react";
import {VOIVODESHIPS} from "./PolandMap.tsx";
import ChartStatusOverlay from "./ChartStatusOverlay.tsx";
import {round} from "../../../util/utils.ts";
import style from "../Home.module.scss";
import {Row} from "react-bootstrap";
import {range} from "lodash";

interface ChartData {
  year: number;
  vehicles?: number;
  NO2?: number;
  NOx?: number;
  PM2_5?: number;
  Pb_PM10?: number;
  SO2?: number;
}

export default function MainLineChart() {
  const [selectedVoivodeship, setSelectedVoivodeship] = useState<string>(VOIVODESHIPS[0]);
  const [startYear, setStartYear] = useState<number>(1990);
  const [endYear, setEndYear] = useState<number>(2019);
  const mergedApiData = useMergedData();
  const cumulativeApiData = useCumulativeData();

  // I need to transform mergedData and cumulativeData into a format that can be used by the LineChart component
  // get cumulative registered (subtract deregistration) vehidles for each year and air pollution data from merged dataset
  const chartData = useMemo(() => {
    const [mergedData, cumulativeData] = [mergedApiData.data, cumulativeApiData.data];
    if (!mergedData || !cumulativeData) {
      return [];
    }

    const result: ChartData[] = [];

    let lastYearData: ChartData = {year: -1}

    for (const [year, yearData] of Object.entries(mergedData)) {
      const numberYear = Number(year);
      if(numberYear < startYear || numberYear > endYear || numberYear < 1900 || numberYear > 2020)
        continue

      const voivodeshipData = yearData[selectedVoivodeship];
      const cumulativeVoivodeshipData = cumulativeData[year][selectedVoivodeship];
      if (!voivodeshipData || !cumulativeVoivodeshipData) {
        continue;
      }

      const newYearData = {
        year: Number(year),
        vehicles: cumulativeVoivodeshipData.registrations && cumulativeVoivodeshipData.deregistrations ?
          round((cumulativeVoivodeshipData.registrations - cumulativeVoivodeshipData.deregistrations) / 100_000) : lastYearData.vehicles,
        NO2: voivodeshipData.NO2 ? round(voivodeshipData.NO2) : lastYearData.NO2,
        NOx: voivodeshipData.NOx ? round(voivodeshipData.NOx) : lastYearData.NOx,
        PM2_5: voivodeshipData["PM2.5"] ? round(voivodeshipData["PM2.5"]) : lastYearData.PM2_5,
        Pb_PM10: voivodeshipData["Pb(PM10)"] ? round(voivodeshipData["Pb(PM10)"] * 100) : lastYearData.Pb_PM10,
        SO2: voivodeshipData.SO2 ? round(voivodeshipData.SO2) : lastYearData.SO2,
      }

      result.push(newYearData);
      lastYearData = newYearData;
    }

    return result;
  }, [mergedApiData, cumulativeApiData, selectedVoivodeship, startYear, endYear])

  return (
    <div className={"col-12 col-lg-10 mx-auto position-relative mt-5"}>
      <h3 className={style.chartTitle}>
        Zarejestrowane pojazdy oraz zanieczyszczenie powietrza w {selectedVoivodeship.slice(0, -1)}m
      </h3>
      <Row className={"col-12 col-lg-8 mx-auto mb-3"}>
        <div className={"col-6"}>
          <label>
            Wybierz województwo:
          </label>
          <select
            value={selectedVoivodeship}
            onChange={e => setSelectedVoivodeship(e.target.value)}
            className="form-select"
          >
            {VOIVODESHIPS.map((indicator) => (
              <option key={indicator} value={indicator}>
                {indicator}
              </option>
            ))}
          </select>
        </div>
        <div className={"col-3"}>
          <label>
            Rok początkowy:
          </label>
          <select
            value={startYear}
            onChange={e => setStartYear(Number(e.target.value))}
            className="form-select"
          >
            {range(1900, endYear ?? 2020).map(year => (
              <option key={year} value={year}>
                {year}
              </option>
            ))}
          </select>
        </div>
        <div className={"col-3"}>
          <label>
            Rok końcowy:
          </label>
          <select
            value={endYear}
            onChange={e => setEndYear(Number(e.target.value))}
            className="form-select"
          >
            {range(startYear ?? 1900, 2020).map(year => (
              <option key={year} value={year}>
                {year}
              </option>
            ))}
          </select>
        </div>
      </Row>
      <ResponsiveContainer width={"100%"} height={500}>
        <LineChart width={730} height={250} data={chartData}
                   margin={{top: 5, right: 30, left: 20, bottom: 5}}>
          <CartesianGrid strokeDasharray="3 3"/>
          <XAxis dataKey="year"/>
          <YAxis/>
          <Tooltip/>
          <Legend/>
          <Line type="monotoneX" dataKey="vehicles" name={"Zarejestrowane pojazdy (w 100k)"} stroke="#8884d8"/>
          <Line type="monotoneX" dataKey="NO2" stroke="#82ca9d"/>
          <Line type="monotoneX" dataKey="NOx" stroke="#ff0000"/>
          <Line type="monotoneX" dataKey="PM2_5" stroke="#0000ff"/>
          <Line type="monotoneX" dataKey="Pb_PM10" stroke="#00ff00"/>
          <Line type="monotoneX" dataKey="SO2" stroke="#ff00ff"/>
        </LineChart>
      </ResponsiveContainer>
      <ChartStatusOverlay apiData={[mergedApiData, cumulativeApiData]}/>
    </div>
  );
}