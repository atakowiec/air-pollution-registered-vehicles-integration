import Container from "react-bootstrap/Container";
import {ProgressBar} from "react-bootstrap";
import {formatDate, formatDuration, formatNumber, round, translateImportStatus} from "../../../util/utils.ts";
import useProgress, {ProgressStatus} from "../../../hooks/useProgress.ts";
import {useMemo} from "react";

export interface ImportProgressData {
  startTime?: number
  readTime?: number
  loadTime?: number
  loaded: boolean;
  endTime?: number
  total: number;
  saved: number;
  read: number;
  indicatorsStatus: {
    [indicator: string]: {
      read: number;
      total: number;
      saved: number;
      readTime: number;
      saveTime: number;
    }
  }
}

interface CalculatedData {
  status: ProgressStatus
  startTime?: string
  readTime?: string
  loadTime?: string
  endTime?: string
  loaded: boolean;
  total: number;
  saved: number;
  read: number;
  readApproxTime?: string;
  saveApproxTime?: string;
  totalTime?: string;
  readPerSecond?: number;
  savedPerSecond?: number;
  readProgress?: number;
  saveProgress?: number;

  indicatorsStatus: {
    [indicator: string]: {
      read: number;
      total: number;
      saved: number;
      readTime: number;
      saveTime: number;
      totalTime?: string;
      readProgress?: number;
      saveProgress?: number;
      readApproxTime?: string;
      saveApproxTime?: string;
    }
  }
}

export default function PollutionImportProgressInfo() {
  const [progress] = useProgress<ImportProgressData>("pollution_xlsx_import")

  const calculatedData: CalculatedData = useMemo(() => {
    const data = progress?.data
    if (!data) {
      return {
        status: "NOT_STARTED",
        loaded: false,
        total: 0,
        saved: 0,
        read: 0,
        indicatorsStatus: {}
      }
    }

    const total = data.total ?? 0
    const saved = data.saved ?? 0
    const read = data.read ?? 0
    const loaded = data.loaded ?? false

    const readPerSecond = round(data.read / ((data.readTime ?? Date.now()) - (data.startTime ?? Date.now())) * 1000)
    const savedPerSecond = round(data.saved / ((data.endTime ?? Date.now()) - (data.startTime ?? Date.now())) * 1000)

    const saveApproxTime = savedPerSecond ? formatDuration((total - saved) / savedPerSecond * 1000) : undefined
    const readApproxTime = readPerSecond ? formatDuration((total - read) / readPerSecond * 1000) : undefined
    const totalTime = formatDuration((data.endTime ?? Date.now()) - (data.startTime ?? Date.now()))

    // here I will add the indicatorsStatus for tracking the progress of each indicator
    // but now im not bored to do it

    return {
      status: progress!.status,
      total,
      saved,
      read,
      loaded,
      readPerSecond,
      savedPerSecond,
      readApproxTime,
      saveApproxTime,
      totalTime,
      readProgress: total === 0 ? 0 : read / total * 100,
      saveProgress: total === 0 ? 0 : saved / total * 100,
      startTime: formatDate(data.startTime),
      readTime: formatDate(data.readTime),
      loadTime: formatDate(data.loadTime),
      endTime: formatDate(data.endTime),
      indicatorsStatus: data.indicatorsStatus
    }
  }, [progress?.data ?? {}])

  if (!progress || calculatedData.status == "NOT_STARTED") return null
  return (
    <Container className={"text-center px-0 mt-2 col-12 col-md-8 col-xl-6 col-xxl-5 gap-5 mb-5 pb-5"}>
      <Container className={"bg-light rounded p-2 mb-2"}>
        <h3>Postęp importu</h3>
        <h5>Status: {translateImportStatus(calculatedData.status)}</h5>
        <p className={"mt-4"}>
          Rozpoczęto: {calculatedData.startTime ?? "-"}<br/>
          Zakończono: {calculatedData.endTime ?? "-"}<br/>
          Czas trwania: {calculatedData.totalTime ?? "-"}
        </p>
      </Container>
      <Container className={"bg-light rounded p-4 pb-2 mb-2"}>
        <h4>Odczytanie danych</h4>
        <p>
          Rozpoczęto: {calculatedData.loadTime ?? "-"}<br/>
          Zakończono: {calculatedData.readTime ?? "-"}
        </p>
        <ProgressBar now={calculatedData.readProgress!} label={`${Math.round(calculatedData.readProgress!)}%`}/>
        <p className={"mt-3"}>
          Odczytano: {formatNumber(calculatedData.read!)} z {formatNumber(calculatedData.total!)} rekordów
          ({calculatedData.readApproxTime ?? "---"})<br/>
          Prędkość odczytu: {calculatedData.readPerSecond} rekordów/s
        </p>
      </Container>
      <Container className={"bg-light rounded p-4 pb-2 mb-2"}>
        <h4 className={"mb-4"}>Zapis danych</h4>
        <ProgressBar now={calculatedData.saveProgress!} label={`${Math.round(calculatedData.saveProgress!)}%`}/>
        <p className={"mt-3"}>
          Zapisano: {formatNumber(calculatedData.saved!)} z {formatNumber(calculatedData.total!)} rekordów
          ({calculatedData.saveApproxTime ?? "---"})<br/>
          Prędkość zapisu: {calculatedData.savedPerSecond} rekordów/s
        </p>
      </Container>
    </Container>
  )
}