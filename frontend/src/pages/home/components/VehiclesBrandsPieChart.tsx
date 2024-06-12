import style from "../Home.module.scss";
import { ResponsiveContainer, PieChart, Pie, Tooltip } from "recharts";
import { useMemo, useState } from "react";
import ChartStatusOverlay, { PropsWithApiData } from "./ChartStatusOverlay.tsx";

export default function VehiclesBrandsPieChart({ apiData }: PropsWithApiData) {
  const [_, setActiveIndex] = useState<number | null>(null);

  type PieData = {
    name: string;
    count: number;
  };

  const onPieClick = (data: PieData, index: number) => {
    setActiveIndex(index);
    console.log("Clicked segment:", data);
  };

  const data = useMemo(() => {
    if (!apiData.data || !apiData.loaded) return null;

    const validData = Object.entries(apiData.data).map(([name, count]) => ({
      name: name as string,
      count: count as number,
    }));

    return validData.sort((a: PieData, b: PieData) => b.count - a.count);
  }, [apiData.data, apiData.loaded]);

  const labelStyle = {
    fontSize: "10px"
  };

  return (
    <div className={`col-12 col-xl-6 ${style.chartContainer}`}>
      <h3 className={style.chartTitle}>
        {" "}
        10 najczęsciej zarejestrowanych marek pojazdów
      </h3>
      <ResponsiveContainer width={"100%"} height={300}>
        <PieChart width={730} height={250}>
          <Pie
            data={data ?? []}
            dataKey={"count"}
            nameKey={"name"}
            cx="50%"
            cy="50%"
            outerRadius={80}
            fill="#8884d8"
            label={(entry) => entry.name}
            onClick={onPieClick}
          />
          <Tooltip />
        </PieChart>
      </ResponsiveContainer>
      <ChartStatusOverlay apiData={apiData}/>
      <style>{`
        .recharts-pie-labels {
          font-size: ${labelStyle.fontSize};
        }
      `}</style>
    </div>
  );
}
