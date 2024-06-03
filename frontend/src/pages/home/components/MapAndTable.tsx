import style from "../Home.module.scss";
import PolandMap from "./PolandMap.tsx";
import {Row} from "react-bootstrap";
import ChartStatusOverlay from "./ChartStatusOverlay.tsx";
import {ChangeEvent, useState} from "react";
import {useCumulativeData, useMergedData} from "../hooks/homeHooks.ts";
import MainTable from "./MainTable.tsx";

export const INDICATORS = ["NO2", "NOx", "PM2.5", "Pb(PM10)", "SO2"];
export const YEARS = Array.from({length: 20}, (_, i) => 2000 + i);
export const VOIVODESHIPS = {
  "02": "dolnośląskie",
  "04": "kujawsko-pomorskie",
  "06": "lubelskie",
  "08": "lubuskie",
  "10": "łódzkie",
  "12": "małopolskie",
  "14": "mazowieckie",
  "16": "opolskie",
  "18": "podkarpackie",
  "20": "podlaskie",
  "22": "pomorskie",
  "24": "śląskie",
  "26": "świętokrzyskie",
  "28": "warmińsko-mazurskie",
  "30": "wielkopolskie",
  "32": "zachodniopomorskie",
  XX: "nieokreślone",
};

export default function MapAndTable() {
  const [selectedIndicator, setSelectedIndicator] = useState<string | null>("registrations");
  const [selectedYear, setSelectedYear] = useState<number | null>(null);
  const mergedData = useMergedData()
  const cumulativeData = useCumulativeData();

  console.log(useCumulativeData())
  const finalData = selectedYear ?
    mergedData.data?.[selectedYear] :
    cumulativeData.data ?
      cumulativeData.data[Math.max(...Object.keys(cumulativeData.data).map(Number))] :
      undefined;

  const handleIndicatorChange = (event: ChangeEvent<HTMLSelectElement>) => {
    setSelectedIndicator(event.target.value || null);
  };

  const handleYearChange = (event: ChangeEvent<HTMLSelectElement>) => {
    setSelectedYear(event.target.value ? Number(event.target.value) : null);
  };

  return (
    <>
      <Row>
        <div className="container mb-3">
          <div className="row align-items-center">
            <div className="col-auto text-end pe-3">
              <p className="mb-0">Wskaźnik:</p>
            </div>
            <div className="col-auto text-center">
              <div className="">
                <select
                  value={selectedIndicator || "registrations"}
                  onChange={handleIndicatorChange}
                  className="form-select"
                >
                  <option value="registrations">Zarejestrowane pojazdy</option>
                  <option value="deregistrations">Wyrejestrowane pojazdy</option>
                  {INDICATORS.map((indicator) => (
                    <option key={indicator} value={indicator}>
                      {indicator}
                    </option>
                  ))}
                </select>
              </div>
            </div>
            <div className="col-auto text-end pe-3">
              <p className="mb-0">Rok:</p>
            </div>
            <div className="col-auto text-center">
              <div className="">
                <select
                  value={selectedYear !== null ? selectedYear : ""}
                  onChange={handleYearChange}
                  className="form-select"
                >
                  <option value="">Wybierz rok</option>
                  {YEARS.map((year) => (
                    <option key={year} value={year}>
                      {year}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>
        </div>
      </Row>
      <Row>
        <div className={`text-center col-12 col-xxl-5 ${style.map} position-relative`}>
          <ChartStatusOverlay apiData={mergedData} backgroundType={"none"}/>
          <PolandMap
            data={finalData}
            selectedIndicator={selectedIndicator} />
        </div>
        <MainTable
          data={finalData}
          selectedIndicator={selectedIndicator}/>
      </Row>
    </>
  );
}
