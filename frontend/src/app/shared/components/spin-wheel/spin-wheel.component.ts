import { Component, Input, Output, EventEmitter } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-spin-wheel',
  templateUrl: './spin-wheel.component.html',
  styleUrls: ['./spin-wheel.component.scss']
})
export class SpinWheelComponent {
  @Input() canSpin = false;
  @Output() spinComplete = new EventEmitter<number>();

  spinning = false;
  rotation = 0;
  segments = [
    { label: '50 DT', color: '#8B9D7A' },
    { label: '100 DT', color: '#C8956C' },
    { label: '75 DT', color: '#D4A853' },
    { label: '200 DT', color: '#8B9D7A' },
    { label: '150 DT', color: '#C8956C' },
    { label: '300 DT', color: '#D4A853' },
    { label: '100 DT', color: '#8B9D7A' },
    { label: '500 DT', color: '#2A2118' },
  ];

  constructor(private http: HttpClient) {}

  getSlicePath(index: number): string {
    const total = this.segments.length;
    const angle = (2 * Math.PI) / total;
    const startAngle = index * angle - Math.PI / 2;
    const endAngle = startAngle + angle;
    const cx = 150, cy = 150, r = 148;
    const x1 = cx + r * Math.cos(startAngle);
    const y1 = cy + r * Math.sin(startAngle);
    const x2 = cx + r * Math.cos(endAngle);
    const y2 = cy + r * Math.sin(endAngle);
    return `M ${cx} ${cy} L ${x1} ${y1} A ${r} ${r} 0 0 1 ${x2} ${y2} Z`;
  }

  getTextTransform(index: number): string {
    const total = this.segments.length;
    const angle = (2 * Math.PI) / total;
    const midAngle = index * angle + angle / 2 - Math.PI / 2;
    const r = 95;
    const cx = 150, cy = 150;
    const x = cx + r * Math.cos(midAngle);
    const y = cy + r * Math.sin(midAngle);
    const deg = (midAngle * 180 / Math.PI) + 90;
    return `translate(${x}, ${y}) rotate(${deg})`;
  }

  spin(): void {
    if (!this.canSpin || this.spinning) return;
    this.spinning = true;

    this.http.post<{ tokensWon: number; newBalance: number }>(`${environment.apiUrl}/rewards/spin`, {}).subscribe({
      next: (res) => {
        const extraSpins = 5 + Math.random() * 3;
        this.rotation = this.rotation + (extraSpins * 360) + (Math.random() * 360);

        setTimeout(() => {
          this.spinning = false;
          this.spinComplete.emit(res.tokensWon);
        }, 3500);
      },
      error: () => { this.spinning = false; }
    });
  }
}
