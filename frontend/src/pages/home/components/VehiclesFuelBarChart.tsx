import style from "../Home.module.scss";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  ReferenceLine,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { useMemo } from "react";
import ChartStatusOverlay, { PropsWithApiData } from "./ChartStatusOverlay.tsx";

export default function VehiclesFuelBarChart({ apiData }: PropsWithApiData) {
  // Transformacja danych do formatu oczekiwanego przez komponent wykresu
  const data = useMemo(() => {
    if (!apiData.data || !apiData.loaded) return null;

    return Object.keys(apiData.data)
      .map((key) => ({
        name: key,
        count: apiData.data[key],
      }))
      .sort((a, b) => b.count - a.count);
  }, [apiData.data, apiData.loaded]);

  // Funkcja do formatowania wartości na osi Y
  const formatYAxis = (tick: number) => {
    if (tick >= 1000000) {
      return `${(tick / 1000000).toFixed(0)} M`; // Konwertuj wartość na miliony
    } else if (tick >= 10000) {
      return `${(tick / 1000).toFixed(0)} tys.`; // Konwertuj wartość na tysiące
    }
    return tick.toString(); // Zwróć wartość jako string
  };

  return (
    <div className={`col-12 col-xl-6 ${style.chartContainer}`}>
      <h3 className={style.chartTitle}>
        Typy paliwa zarejestrowanych pojazdów w Polsce
      </h3>
      <ResponsiveContainer width={"100%"} height={400}>
        <BarChart data={data ?? []} className={"col-6"}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey={"name"} hide={true}/>
          <YAxis
            scale="log"
            domain={[1, 30000000]}
            tickFormatter={formatYAxis}
          />
          <Tooltip
            formatter={(value) =>
              new Intl.NumberFormat("pl").format(Number(value))
            }
          />
          <Legend />
          <Bar dataKey="count" name={"Typ paliwa"} fill="#8884d8" />
          <ReferenceLine
            y={14000000}
            stroke="black"
            label="14M"
            strokeDasharray="3 3"
          />
        </BarChart>
      </ResponsiveContainer>
      <ChartStatusOverlay apiData={apiData} />
    </div>
  );
}
