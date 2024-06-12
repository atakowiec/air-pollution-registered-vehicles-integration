import style from "../Home.module.scss";
import {Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis} from "recharts";
import {useMemo} from "react";
import ChartStatusOverlay, {PropsWithApiData} from "./ChartStatusOverlay.tsx";

export default function VehiclesBarChart({apiData}: PropsWithApiData) {
  // first we need to transform the data to the format that the chart component expects
  const data = useMemo(() => {
    if (!apiData.data || !apiData.loaded) return null

    return Object.keys(apiData.data).map(key => ({
      name: key,
      count: apiData.data[key],
    })).sort((a, b) => b.count-a.count)
  }, [apiData.data, apiData.loaded])

  return (
    <div className={`col-12 col-xl-6 ${style.chartContainer}`}>
      <h3 className={style.chartTitle}>
        Zarejestrowane pojazdy poszczególnych województwach (stan na 2019)
      </h3>
      <ResponsiveContainer width={"100%"} height={300}>
        <BarChart data={data ?? []} className={"col-6"}>
          <CartesianGrid strokeDasharray="3 3"/>
          <XAxis dataKey={"name"} hide={true}/>
          <YAxis tickFormatter={(tick) => `${tick / 1000000}M`} />
          <Tooltip/>
          <Legend/>
          <Bar dataKey="count" name={"Zarejestowane pojazdy"} fill="#8884d8"/>
        </BarChart>
      </ResponsiveContainer>
      <ChartStatusOverlay apiData={apiData}/>
    </div>
  )
}