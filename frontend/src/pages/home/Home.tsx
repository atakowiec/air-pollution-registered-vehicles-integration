import {MainNavbar} from "../../components/MainNavbar.tsx";
import {title} from "../../util/title.ts";
import PolandMap from "../../components/PolandMap.tsx";
import useApi from "../../hooks/useApi.ts";
import {Row, Table} from "react-bootstrap";
import {formatNumber} from "../../util/utils.ts";
import Container from "react-bootstrap/Container";
import style from "./Home.module.scss";
import {
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  BarChart,
  Legend,
  Bar,
  ResponsiveContainer,
  AreaChart,
  Area, PieChart, Pie, Radar, PolarAngleAxis, PolarRadiusAxis, PolarGrid, RadarChart
} from "recharts";

// todo its just an sample data for charts, remove it
const SAMPLE_DATA = Array.from({length: 16}, (_, i) => ({
  name: `name-${i}`,
  value1: Math.floor(Math.random() * 1000),
  value2: Math.floor(Math.random() * 1000)
}))

export default function Home() {
  title("Home")
  const {data, loaded} = useApi("/vehicles/counts/by-area-code", "get")

  if (!loaded) return null

  return (
    <>
      <MainNavbar/>
      <Container className={"mt-5 col-12 col-xl-8 mx-auto mb-5"}>
        <Row>
          <div className={`text-center col-12 col-xl-5 ${style.map}`}>
            <PolandMap data={data}/>
          </div>
          <div className={`col-12 col-xl-7 ${style.table}`}>
            <Table bordered striped>
              <thead>
              <tr>
                <th>Kod pocztowy</th>
                <th>Liczba pojazdów</th>
                <th>Jakies inne dane</th>
              </tr>
              </thead>
              <tbody>
              {Object.keys(data).map((key) => (
                <tr key={key}>
                  <td>{key}</td>
                  <td>{formatNumber(data[key])}</td>
                  <td>...</td>
                </tr>
              ))}
              </tbody>
            </Table>
          </div>
        </Row>
        <Row className={"mt-5"}>
          <div className={"col-12 col-xl-6"}>
            <h3 className={style.chartTitle}>
              Przykładowy wykres słupkowy
            </h3>
            <ResponsiveContainer width={"100%"} height={300} className={"rounded-2"}>
              {/*this chart does not work well with this number of data points (names are too long), but we will find out how to fix it*/}
              <BarChart data={SAMPLE_DATA} className={"col-6"}>
                <CartesianGrid strokeDasharray="3 3"/>
                <XAxis dataKey={"name"}/>
                <YAxis/>
                <Tooltip/>
                <Legend/>
                <Bar dataKey="value1" fill="#8884d8"/>
                <Bar dataKey="value2" fill="#82ca9d"/>
              </BarChart>
            </ResponsiveContainer>
          </div>
          <div className={"col-12 col-xl-6"}>
            <h3 className={style.chartTitle}>
              Przykładowy wykres liniowy
            </h3>
            <ResponsiveContainer width={"100%"} height={300}>
              {/*problem same as above*/}
              <AreaChart width={730} height={250} data={SAMPLE_DATA}
                         margin={{top: 10, right: 30, left: 0, bottom: 0}}>
                <defs>
                  <linearGradient id="colorUv" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#8884d8" stopOpacity={0.8}/>
                    <stop offset="95%" stopColor="#8884d8" stopOpacity={0}/>
                  </linearGradient>
                  <linearGradient id="colorPv" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#82ca9d" stopOpacity={0.8}/>
                    <stop offset="95%" stopColor="#82ca9d" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <XAxis dataKey="name"/>
                <YAxis/>
                <CartesianGrid strokeDasharray="3 3"/>
                <Tooltip/>
                <Legend/>
                <Area type="monotone" dataKey="value1" stroke="#8884d8" fillOpacity={1} fill="url(#colorUv)"/>
                <Area type="monotone" dataKey="value2" stroke="#82ca9d" fillOpacity={1} fill="url(#colorPv)"/>
              </AreaChart>
            </ResponsiveContainer>
          </div>
          <div className={"col-12 col-xl-6"}>
            <h3 className={style.chartTitle}>
              Przykładowy wykres kołowy
            </h3>
            <ResponsiveContainer width={"100%"} height={300}>
              <PieChart width={730} height={250}>
                {/*this chart is a bit broken, but it's just an example*/}
                <Pie data={SAMPLE_DATA} dataKey="value1" nameKey="name" cx="50%" cy="50%" outerRadius={80}
                     fill="#8884d8"
                     label/>
              </PieChart>
            </ResponsiveContainer>
          </div>
          <div className={"col-12 col-xl-6"}>
            <h3 className={style.chartTitle}>
              Przykładowy wykres radarowy
            </h3>
            <ResponsiveContainer width={"100%"} height={300}>
              {/*this chart is not made for this number of data points, but still it's just an example*/}
              <RadarChart outerRadius={90} width={730} height={250} data={SAMPLE_DATA}>
                <PolarGrid/>
                <PolarAngleAxis dataKey="name"/>
                <Radar name="value1" dataKey="value1" stroke="#8884d8" fill="#8884d8" fillOpacity={0.6}/>
                <Radar name="value2" dataKey="value2" stroke="#82ca9d" fill="#82ca9d" fillOpacity={0.6}/>
                <PolarRadiusAxis angle={30} domain={[0, 150]}/>
                <Legend/>
              </RadarChart>
            </ResponsiveContainer>
          </div>
        </Row>
      </Container>
    </>
  )
}